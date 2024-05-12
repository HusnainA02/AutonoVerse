package com.example.autonoverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast

class MisAnuncios : AppCompatActivity() {

    private lateinit var Btn_btnVerMisAnuncios:Button
    private lateinit var Btn_btnVerMisSolicitudes:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_anuncios)
        Btn_btnVerMisAnuncios=findViewById(R.id.btnVerMisAnun)
        Btn_btnVerMisSolicitudes=findViewById(R.id.btnVerMisSoli)

        Btn_btnVerMisAnuncios.setOnClickListener{
            val intent=Intent(this@MisAnuncios,VerMisAnuncios::class.java)
            Toast.makeText(applicationContext, "Ver mis anuncios", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_btnVerMisSolicitudes.setOnClickListener{
            val intent=Intent(this@MisAnuncios,VerMisSolicitudes::class.java)
            Toast.makeText(applicationContext, "Ver mis solicitudes", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

}


