package com.example.autonoverse

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var Btn_buscaRegistro: Button
    private lateinit var correoL: EditText
    private lateinit var passL: EditText
    private lateinit var checkBoxPoliticas: CheckBox
    private lateinit var LinkPoliticasPriv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        correoL = findViewById(R.id.CorreoUsuLogin)
        passL = findViewById(R.id.PassUsuLogin)
        Btn_buscaRegistro = findViewById(R.id.btnLogin)
        checkBoxPoliticas = findViewById(R.id.CheckBoxPoliticas)
        LinkPoliticasPriv = findViewById(R.id.LinkPoliticas)
        Btn_buscaRegistro.setOnClickListener {
            buscaDatosUsuarios()
        }
        LinkPoliticasPriv.setOnClickListener {
            val url = "https://vedrunavall.cat"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

    }

    private fun buscaDatosUsuarios() {
        val correo = correoL.text.toString().trim()
        val password = passL.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el correo electrónico y la contraseña", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, cargar datos adicionales o redirigir al usuario
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        // Cargar datos adicionales o redirigir
                        cargarDatosAdicionales(user.uid)
                    }
                } else {
                    // Error de autenticación, mostrar un mensaje adecuado
                    if(task.exception?.message?.contains("no user") == true) {
                        mostrarDialogo("Error", "Correo electrónico no encontrado")
                    } else {
                        Toast.makeText(this, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun cargarDatosAdicionales(uid: String) {
        val database = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val nombreUsuario = snapshot.child("nombre").value.toString()
                Toast.makeText(this, "Bienvenido/a $nombreUsuario", Toast.LENGTH_LONG).show()
                // Proceder a la siguiente actividad
                val intent = Intent(this, MiPerfil::class.java)
                intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                startActivity(intent)
            } else {
                mostrarDialogo("Error", "No se encontraron datos adicionales.")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar datos adicionales: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        val alertDialogBuilder = AlertDialog.Builder(this@Login)
        alertDialogBuilder.setTitle(titulo)
        alertDialogBuilder.setMessage(mensaje)
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
