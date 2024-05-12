package com.example.autonoverse

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerMisSolicitudesCOPIA : AppCompatActivity() {

    private lateinit var userIdEditText: EditText
    private lateinit var userPasswordEditText: EditText
    private lateinit var verifyButton: Button
    private lateinit var solicitudesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mis_solicitudes)

        userIdEditText = findViewById(R.id.etUserId)
        userPasswordEditText = findViewById(R.id.etUserPassword)
        verifyButton = findViewById(R.id.btnVerify)
        solicitudesLayout = findViewById(R.id.linearLayoutSolicitudes)

        verifyButton.setOnClickListener {
            val userId = userIdEditText.text.toString().trim()
            val password = userPasswordEditText.text.toString().trim()
            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                verifyUserCredentials(userId, password)
            }
        }
    }

    private fun verifyUserCredentials(userId: String, password: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("Usuarios")
        usersRef.orderByChild("idUsuario").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { userSnapshot ->
                        val storedPassword = userSnapshot.child("Contrasenya").value.toString()
                        if (storedPassword == password) {
                            Toast.makeText(this@VerMisSolicitudesCOPIA, "Verificación exitosa. Mostrando solicitudes...", Toast.LENGTH_SHORT).show()
                            displayUserRequests(userId)
                        } else {
                            showAlert("Error", "Contraseña incorrecta.")
                        }
                    }
                } else {
                    showAlert("Error", "ID de usuario no encontrado.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showAlert("Error", "Error al acceder a la base de datos: ${error.message}")
            }
        })
    }

    private fun displayUserRequests(userId: String) {
        // Aquí implementarías la carga y visualización de las solicitudes de contacto
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}