package com.example.autonoverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.widget.Button
import androidx.appcompat.widget.Toolbar


class Trabajar : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var searchView: SearchView
    private var allAnuncios: MutableList<AnuncioBBDD> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trabajar)

        // Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        linearLayout = findViewById(R.id.linearLayout)
        searchView = findViewById(R.id.searchView)
        setupSearchView()
        cargarAnunciosDesdeFirebase()
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Esto hará que funcional al botón de atrás
        return true
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAnuncios(newText)
                return true
            }
        })
    }

    private fun filterAnuncios(text: String?) {
        linearLayout.removeAllViews()
        val filteredList = if (text.isNullOrEmpty()) {
            allAnuncios
        } else {
            allAnuncios.filter {
                it.titulo.contains(text, ignoreCase = true) || it.referencia.contains(text, ignoreCase = true)
            }
        }
        filteredList.forEach { anuncio ->
            val view = LayoutInflater.from(this).inflate(R.layout.lista_anuncios, linearLayout, false)
            populateViewWithData(view, anuncio)
            linearLayout.addView(view)
        }
    }

    private fun cargarAnunciosDesdeFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Anuncios")
        databaseReference.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allAnuncios.clear()
                dataSnapshot.children.reversed().forEach { snapshot ->
                    val anuncio = snapshot.getValue(AnuncioBBDD::class.java)
                    anuncio?.let { allAnuncios.add(it) }
                }
                filterAnuncios(searchView.query.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Trabajar, "Error al cargar datos de Firebase: ${databaseError.message}", Toast.LENGTH_LONG).show()
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
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(anuncio.timestamp)
        view.findViewById<TextView>(R.id.FechaTrab).text = "Fecha: $fecha"

        view.findViewById<Button>(R.id.btnContactTrab).setOnClickListener {
            val intent = Intent(this, FormularioContacto::class.java)
            intent.putExtra("Referencia", anuncio.referencia)
            intent.putExtra("Titulo", anuncio.titulo)
            intent.putExtra("IdUsuario", anuncio.idUsuario)
            startActivity(intent)
        }

    }
}
