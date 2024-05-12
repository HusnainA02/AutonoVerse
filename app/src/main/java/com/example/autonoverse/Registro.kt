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
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random
import java.util.UUID

class Registro : AppCompatActivity() {

    private lateinit var Btn_EnviarRegistro: Button
    private lateinit var idU: EditText
    private lateinit var nombreU: EditText
    private lateinit var correoU: EditText
    private lateinit var passU: EditText
    private lateinit var pass2U: EditText
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var checkBoxPoliticas: CheckBox
    private lateinit var LinkPoliticasPriv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        idU = findViewById(R.id.IdUsu)
        nombreU = findViewById(R.id.NombreUsuario)
        correoU = findViewById(R.id.CorreoUsuario)
        passU = findViewById(R.id.PassUsuario)
        pass2U = findViewById(R.id.Pass2Usuario)
        checkBoxPoliticas = findViewById(R.id.CheckBoxPoliticas)
        Btn_EnviarRegistro = findViewById(R.id.btnRegistro)
        LinkPoliticasPriv = findViewById(R.id.LinkPoliticas)


        // Utiliza el correo electrónico como la clave principal en la base de datos
        database = FirebaseDatabase.getInstance().getReference("Usuarios")
        auth = FirebaseAuth.getInstance()


        // Generar y mostrar la referencia aleatoria
        val generatedUserId = findViewById<EditText>(R.id.IdUsu)
        idU.setText(generatedUserId())
        idU.isEnabled = false // Deshabilitar la edición del campo

        Btn_EnviarRegistro.setOnClickListener {
            if (validarRegistro()) {
                val usuarioCorreo = correoU.text.toString()
                verificarCorreoEnDatabase(usuarioCorreo)
            }
        }
        LinkPoliticasPriv.setOnClickListener {
            val url = "https://vedrunavall.cat"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

    }

    private fun generatedUserId(): String {
        val random = Random()
        return String.format("%04d", random.nextInt(1000))
    }

    private fun validarRegistro(): Boolean {
        val usuarioNombre = nombreU.text.toString()
        val usuarioCorreo = correoU.text.toString()
        val usuarioPass = passU.text.toString()
        val usuarioPass2 = pass2U.text.toString()
        val checkBoxPoliticas = checkBoxPoliticas.isChecked

        if ( usuarioNombre.isEmpty() || usuarioCorreo.isEmpty() || usuarioPass.isEmpty() || usuarioPass2.isEmpty() || !checkBoxPoliticas) {
            mostrarDialogo("Error de validación", "Por favor complete todos los campos")
            return false
        }

        // Verificar que las contraseñas coincidan
        if (usuarioPass != usuarioPass2) {
            mostrarDialogo("Error de validación", "Las contraseñas no coinciden")
            return false
        }

        // Validar el correo electrónico
        val emailError = validEmail(usuarioCorreo)
        if (emailError != null) {
            mostrarDialogo("Error de validación", emailError)
            return false
        }

        // Validar la contraseña
        val passwordError = validPassword(usuarioPass)
        if (passwordError != null) {
            mostrarDialogo("Error de validación", passwordError)
            return false
        }

        return true
    }



    private fun registrarEnFirebaseAuth(correo: String, contraseña: String) {
        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Usuario registrado con éxito")
                    guardaDatosUsuarios()
                } else {
                    Toast.makeText(baseContext, "Autenticación fallida: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun verificarCorreoEnDatabase(correo: String) {
        database.orderByChild("correo").equalTo(correo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mostrarDialogo("Error", "El correo electrónico ya está registrado.")
                } else {
                    val usuarioPass = passU.text.toString()
                    registrarEnFirebaseAuth(correo, usuarioPass)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mostrarDialogo("Error", "Error al acceder a la base de datos: ${error.message}")
            }
        })
    }

    private fun guardaDatosUsuarios() {
        val usuId = idU.text.toString()
        val usuarioNombre = nombreU.text.toString()
        val usuarioCorreo = correoU.text.toString()
        val usuarioPass = passU.text.toString()
        val firebaseUser = auth.currentUser

        val usuarioId = firebaseUser?.uid ?: database.push().key ?: ""
        // Utiliza el correo electrónico como clave
        val uBBDD = EstructuraBBDD(usuId, usuarioNombre, usuarioCorreo, usuarioPass)

        // Utiliza el correo electrónico como clave al guardar en la base de datos
        database.child(usuarioId).setValue(uBBDD)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mostrarDialogoOtrapantalla("Éxito", "Datos insertados correctamente")
                } else {
                    mostrarDialogo("Error", "Error al insertar datos ${task.exception?.message}")
                }
            }
    }

    private fun validEmail(email: String): String? {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Dirección de Email incorrecta"
        }
        return null
    }

    private fun validPassword(password: String): String? {
        if (password.length < 8) {
            return "Mínimo 8 caracteres en la contraseña"
        }
        if (!password.matches(".*[A-Z].*".toRegex())) {
            return "Debe contener al menos una letra mayúscula"
        }
        if (!password.matches(".*[a-z].*".toRegex())) {
            return "Debe contener al menos una letra minúscula"
        }
        if (!password.matches(".*[@#\$%&+=*!/!%()?].*".toRegex())) {
            return "Debe contener al menos un caracter especial en la contraseña (@#\$%&+=*!/!%()?)"
        }
        return null
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun mostrarDialogoOtrapantalla(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { _, _ ->
                // Redirigir a la pantalla de inicio de sesión
                val intent = Intent(this@Registro, Login::class.java)
                startActivity(intent)
                limpiarCampos()
            }
            .show()
    }

    private fun limpiarCampos() {
        idU.text.clear()
        nombreU.text.clear()
        correoU.text.clear()
        passU.text.clear()
        pass2U.text.clear()
        checkBoxPoliticas.isChecked = false
    }
}
