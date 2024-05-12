package com.example.autonoverse

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*

class VerMisSolicitudes : AppCompatActivity() {

    private lateinit var solicitudes: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mis_solicitudes)
        solicitudes = findViewById(R.id.MisSolicitudes)

        cargarSolicitudes()

        // Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Esto hará que funcional al botón de atrás
        return true
    }

    private fun cargarSolicitudes() {
        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("UserId", null)  // Asegúrate de manejar null correctamente

        if (userId == null) {
            // Manejar el caso en que no se encontró el userId
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("ContactoAnuncios")
        databaseReference.orderByChild("id").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                solicitudes.removeAllViews()
                for (solicitudSnapshot in snapshot.children) {
                    val solicitud = solicitudSnapshot.getValue(ContactoBBDD::class.java)
                    solicitud?.let { agregarSolicitudVista(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun agregarSolicitudVista(solicitud: ContactoBBDD) {
        val view = LayoutInflater.from(this).inflate(R.layout.lista_solicitudes, solicitudes, false)
        view.findViewById<TextView>(R.id.Referencia).text = solicitud.referencia
        view.findViewById<TextView>(R.id.Titulo).text = solicitud.titulo
        view.findViewById<TextView>(R.id.IdUsuario).text = solicitud.id
        view.findViewById<TextView>(R.id.Correo).text = solicitud.correo
        view.findViewById<TextView>(R.id.Numero).text = solicitud.numero
        view.findViewById<TextView>(R.id.HorasDisponibles).text = solicitud.horasDisponibles
        view.findViewById<TextView>(R.id.Otros).text = solicitud.otros
        val tvUrlArchivo = view.findViewById<TextView>(R.id.UrlArchivo)
        tvUrlArchivo.text = solicitud.urlArchivo
        tvUrlArchivo.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(solicitud.urlArchivo))
            startActivity(browserIntent)
        }
        solicitudes.addView(view)
    }
}
