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
import android.content.Context
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.Locale

class VerMisAnuncios : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Anuncios")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mis_anuncios)
        linearLayout = findViewById(R.id.verMisAnuncios)

        // Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Recuperar la ID de usuario de las preferencias compartidas
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("UserId", null)

        if (userId != null) {
            cargarAnunciosPorUsuarioId(userId)
        } else {
            mostrarDialogo("Error", "No se pudo recuperar la ID de usuario. Asegúrese de estar registrado e iniciar sesión correctamente.")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Esto hará que funcional al botón de atrás
        return true
    }

    private fun cargarAnunciosPorUsuarioId(userId: String) {
        databaseReference.orderByChild("idUsuario").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                linearLayout.removeAllViews()
                if (!dataSnapshot.exists()) {
                    mostrarDialogo("Resultado", "No se encontraron anuncios para este usuario.")
                    return
                }
                try {
                    dataSnapshot.children.forEach { snapshot ->
                        val anuncio = snapshot.getValue(AnuncioBBDD::class.java)
                        anuncio?.let {
                            val view = LayoutInflater.from(this@VerMisAnuncios).inflate(R.layout.lista_mis_anuncios, linearLayout, false)
                            populateViewWithData(view, it)
                            linearLayout.addView(view)
                        }
                    }
                } catch (e: Exception) {
                    mostrarDialogo("Error", "Ha ocurrido un error al cargar los anuncios: ${e.message}")
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
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(anuncio.timestamp)
        view.findViewById<TextView>(R.id.FechaTrab).text = "Fecha: $fecha"
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
