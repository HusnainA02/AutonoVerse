package com.example.autonoverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerMisAnunciosCOPIA2 : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Anuncios")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mis_anuncios)
        linearLayout = findViewById(R.id.verMisAnuncios)

        // Cargar anuncios del usuario autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            cargarAnunciosPorUsuarioId(currentUser.uid)
        } else {
            mostrarDialogo("Error", "Usuario no autenticado. Por favor, inicie sesión.")
        }
    }

    private fun cargarAnunciosPorUsuarioId(userId: String) {
        databaseReference.orderByChild("idUsuario").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                linearLayout.removeAllViews()
                if (!dataSnapshot.exists()) {
                    mostrarDialogo("Resultado", "No se encontraron anuncios para este usuario.")
                    return
                }
                for (snapshot in dataSnapshot.children) {
                    val anuncio = snapshot.getValue(AnuncioBBDD::class.java)
                    anuncio?.let {
                        val view = LayoutInflater.from(this@VerMisAnunciosCOPIA2).inflate(R.layout.lista_anuncios, linearLayout, false)
                        populateViewWithData(view, it)
                        linearLayout.addView(view)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mostrarDialogo("Error al cargar anuncios", databaseError.message)
            }
        })
    }

    private fun populateViewWithData(view: View, anuncio: AnuncioBBDD) {
        view.findViewById<TextView>(R.id.TitTrab).text = anuncio.titulo
        view.findViewById<TextView>(R.id.RefUsuTrab).text = "Ref.: ${anuncio.referencia}"
        view.findViewById<TextView>(R.id.LocUsuTrab).text = "Ubicación: ${anuncio.ciudadAnuncio}"
        view.findViewById<TextView>(R.id.PrecioTrab).text = "A partir de: ${anuncio.precioAnuncio}€ / h"
        view.findViewById<TextView>(R.id.DisponTrab).text = "Necesita: ${anuncio.disponibilidadAnuncio}h disponibilidad"
        view.findViewById<TextView>(R.id.DescripTrab).text = "Descripción: ${anuncio.descripcion}"
        view.findViewById<TextView>(R.id.ExperiTrab).text = "Experiencia necesaria: ${anuncio.experiencia}"
        view.findViewById<TextView>(R.id.IdUsuTrab).text = "ID de usuario: ${anuncio.idUsuario}"
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
