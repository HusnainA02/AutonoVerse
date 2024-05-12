package com.example.autonoverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize

class MainActivity : AppCompatActivity() {

    private lateinit var Btn_registrarse:Button
    private lateinit var Btn_logearse:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        Btn_registrarse=findViewById(R.id.btnRegistro)
        Btn_logearse=findViewById(R.id.btnLogin)

        Btn_registrarse.setOnClickListener{
            val intent=Intent(this@MainActivity,Registro::class.java)
            Toast.makeText(applicationContext, "Registro", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_logearse.setOnClickListener{
            val intent=Intent(this@MainActivity,Login::class.java)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

}



