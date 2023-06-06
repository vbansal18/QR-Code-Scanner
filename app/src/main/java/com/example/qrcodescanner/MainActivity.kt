package com.example.qrcodescanner

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.qrButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.setPrompt("Scan QR Code")
            intentIntegrator.initiateScan()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                setLink(result.contents.toString())
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun setLink(link: String) {
        val data = hashMapOf(link to link)

        db.collection("Decoded QRs").document("Values")
            .set(data, SetOptions.merge())


        binding.link.text = "Open Link : ${link}"
        binding.link.visibility = android.view.View.VISIBLE
        binding.link.setOnClickListener{
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(link)
            startActivity(openURL)
        }
    }

}