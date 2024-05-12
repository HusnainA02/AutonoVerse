package com.example.autonoverse

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import android.content.Intent

class MiPerfil : AppCompatActivity() {

    private lateinit var tvIdUsuario: TextView
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var cbMostrarContrasena: CheckBox
    private lateinit var btnGuardarCambios: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var btnSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_perfil)

        btnSiguiente = findViewById(R.id.btnSiguiente)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        tvIdUsuario = findViewById(R.id.etIdUsuario)
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        cbMostrarContrasena = findViewById(R.id.cbMostrarContrasena)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)

        cargarDatosUsuario()

        cbMostrarContrasena.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etContrasena.inputType = 0x90
            } else {
                etContrasena.inputType = 0x81
            }
        }



        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
        btnSiguiente.setOnClickListener {
            // Aquí configuras la acción para abrir la actividad Bienvenida
            val intent = Intent(this, Bienvenida::class.java)
            startActivity(intent)
        }
    }



    private fun cargarDatosUsuario() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = database.getReference("Usuarios").child(user.uid)
            userRef.get().addOnSuccessListener {
                val userId = it.child("ids").value.toString()
                tvIdUsuario.setText(userId)
                etNombre.setText(it.child("nombre").value.toString())
                etCorreo.setText(it.child("correo").value.toString())
                etContrasena.setText(it.child("contrasenya").value.toString())

                // Guardar la ID de usuario en las preferencias compartidas
                val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("UserId", userId).apply()
            }
        }
    }



    private fun guardarCambios() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = database.getReference("Usuarios").child(user.uid)
            userRef.updateChildren(mapOf(
                "ids" to tvIdUsuario.text.toString(),  // Guardar el ID modificado
                "nombre" to etNombre.text.toString(),
                "correo" to etCorreo.text.toString(),
                "contrasenya" to etContrasena.text.toString()
            )).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Datos actualizados con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
