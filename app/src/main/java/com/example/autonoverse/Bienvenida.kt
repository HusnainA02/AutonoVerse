package com.example.autonoverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast

class Bienvenida : AppCompatActivity() {

    private lateinit var Btn_AnunciarTrabajo:Button
    private lateinit var Btn_Trabaja:Button
    private lateinit var Btn_Comentario:Button
    private lateinit var Btn_VerAnun:Button
    private lateinit var Btn_MiPerfil:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)
        Btn_AnunciarTrabajo=findViewById(R.id.btnAnunTrabajo)
        Btn_Trabaja=findViewById(R.id.btnTrabajar)
        Btn_Comentario=findViewById(R.id.btnComent)
        Btn_VerAnun=findViewById(R.id.btnVerAnun)
        Btn_MiPerfil=findViewById(R.id.btnMiPerfil)

        Btn_AnunciarTrabajo.setOnClickListener{
            val intent=Intent(this@Bienvenida,AnunciarTrabajo::class.java)
            Toast.makeText(applicationContext, "Anunciar Trabajo", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_Trabaja.setOnClickListener{
            val intent=Intent(this@Bienvenida,Trabajar::class.java)
            Toast.makeText(applicationContext, "Trabajar", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_Comentario.setOnClickListener{
            val intent=Intent(this@Bienvenida,Comentar::class.java)
            Toast.makeText(applicationContext, "Dejar Comentario", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_VerAnun.setOnClickListener{
            val intent=Intent(this@Bienvenida,MisAnuncios::class.java)
            Toast.makeText(applicationContext, "Mis Anucios", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        Btn_MiPerfil.setOnClickListener{
            val intent=Intent(this@Bienvenida,MiPerfil::class.java)
            Toast.makeText(applicationContext, "Mis Anucios", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }

}


