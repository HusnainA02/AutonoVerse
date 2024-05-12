package com.example.autonoverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerMisAnunciosCOPIA : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var usuarioIdEditText: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mis_anuncios)
        linearLayout = findViewById(R.id.linearLayoutMisAnuncios)
        usuarioIdEditText = findViewById(R.id.usuarioId)
        searchButton = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            val userId = usuarioIdEditText.text.toString()
            if (userId.isNotEmpty()) {
                cargarAnunciosPorUsuarioId(userId)
            } else {
                mostrarDialogo("Error", "Por favor ingrese una ID de usuario.")
            }
        }
    }

    private fun cargarAnunciosPorUsuarioId(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Anuncios")
        databaseReference.orderByChild("idUsuario").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                linearLayout.removeAllViews()
                if (!dataSnapshot.exists()) {
                    mostrarDialogo("Resultado", "No se encontraron anuncios para este usuario.")
                    return
                }
                dataSnapshot.children.forEach { snapshot ->
                    val anuncio = snapshot.getValue(AnuncioBBDD::class.java)
                    anuncio?.let {
                        val view = LayoutInflater.from(this@VerMisAnunciosCOPIA).inflate(R.layout.lista_anuncios, linearLayout, false)
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
        view.findViewById<TextView>(R.id.ExperiTrab).text = "Experiencia que necesita: ${anuncio.experiencia}"
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
