package com.example.autonoverse

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Comentar : AppCompatActivity() {

    private lateinit var btnComentario: Button
    private lateinit var referenciaAnuncio: EditText
    private lateinit var idUsuario: EditText
    private lateinit var experienciaUsuario: EditText
    private lateinit var valoracionUsuario: EditText
    private lateinit var databaseComentarios: DatabaseReference
    private lateinit var databaseAnuncios: DatabaseReference
    private lateinit var databaseUsuarios: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentar)

        btnComentario = findViewById(R.id.btnComen)
        referenciaAnuncio = findViewById(R.id.RefanunComen)
        idUsuario = findViewById(R.id.IdUsuComen)
        experienciaUsuario = findViewById(R.id.ExpComen)
        valoracionUsuario = findViewById(R.id.ValoComen)

        databaseComentarios = FirebaseDatabase.getInstance().reference.child("Comentarios")
        databaseAnuncios = FirebaseDatabase.getInstance().reference.child("Anuncios")
        databaseUsuarios = FirebaseDatabase.getInstance().reference.child("Usuarios")

        btnComentario.setOnClickListener {
            if (validarCampos()) {
                verificarExistenciaAnuncio()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (referenciaAnuncio.text.toString().isEmpty() || idUsuario.text.toString().isEmpty() || experienciaUsuario.text.toString().isEmpty() || valoracionUsuario.text.toString().isEmpty()) {
            mostrarDialogo("Campos obligatorios", "Por favor, complete todos los campos.")
            return false
        }
        return true
    }

    private fun verificarExistenciaAnuncio() {
        val ref = referenciaAnuncio.text.toString()
        databaseAnuncios.child(ref).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                verificarExistenciaUsuario()
            } else {
                mostrarDialogo("Anuncio no encontrado", "La referencia del anuncio no existe.")
            }
        }.addOnFailureListener {
            mostrarDialogo("Error", "Error al verificar la existencia del anuncio.")
        }
    }

    private fun verificarExistenciaUsuario() {
        val id = idUsuario.text.toString()
        databaseUsuarios.child(id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                guardarComentario()
            } else {
                mostrarDialogo("Usuario no encontrado", "El Id del usuario no existe en la base de datos.")
            }
        }.addOnFailureListener {
            mostrarDialogo("Error", "Error al verificar la existencia del usuario.")
        }
    }

    private fun guardarComentario() {
        val comentario = hashMapOf(
            "referenciaAnuncio" to referenciaAnuncio.text.toString(),
            "idUsuario" to idUsuario.text.toString(),
            "experiencia" to experienciaUsuario.text.toString(),
            "valoracion" to valoracionUsuario.text.toString()
        )

        databaseComentarios.push().setValue(comentario).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mostrarDialogo("Comentario enviado", "Tu comentario se ha enviado correctamente. ¡Gracias por tu valoración!") {
                    val intent = Intent(this, Bienvenida::class.java)
                    startActivity(intent)
                }
            } else {
                mostrarDialogo("Error", "Error al guardar el comentario.")
            }
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String, accion: () -> Unit = {}) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(titulo)
        alertDialogBuilder.setMessage(mensaje)
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
            accion()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
