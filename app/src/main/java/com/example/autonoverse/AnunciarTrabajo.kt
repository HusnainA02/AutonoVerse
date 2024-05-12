package com.example.autonoverse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.app.AlertDialog
import android.net.Uri
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Random
import java.text.SimpleDateFormat
import java.util.Locale


class AnunciarTrabajo : AppCompatActivity() {

    private lateinit var Btn_SiguienteAnunciarTrabajo: Button
    private lateinit var tituloAnuncio: EditText
    private lateinit var descripcion: EditText
    private lateinit var experiencia: EditText
    private lateinit var correo: EditText
    private lateinit var idUsuario: EditText
    private lateinit var contacto: EditText
    private lateinit var ciudad: EditText
    private lateinit var precio: EditText
    private lateinit var disponibilidad: EditText
    private lateinit var databaseAnuncios: DatabaseReference
    private lateinit var databaseUsuarios: DatabaseReference
    private lateinit var checkBoxPoliticas: CheckBox
    private lateinit var LinkPoliticasPriv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anunciar_trabajo)

        // Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        Btn_SiguienteAnunciarTrabajo = findViewById(R.id.btnPubliAnunTrab)
        tituloAnuncio = findViewById(R.id.TituloAnun)
        descripcion = findViewById(R.id.DescripAnun)
        experiencia = findViewById(R.id.ExpAnun)
        correo = findViewById(R.id.CorreoAnun)
        idUsuario = findViewById(R.id.IdUsuAnun)
        contacto = findViewById(R.id.ContAnun)
        ciudad = findViewById(R.id.UbiAnun)
        precio = findViewById(R.id.PrecioAnun)
        disponibilidad = findViewById(R.id.DisponAnun)
        checkBoxPoliticas = findViewById(R.id.CheckBoxPoliticas)
        LinkPoliticasPriv = findViewById(R.id.LinkPoliticas)

        // Obtenemos la referencia de la base de datos de Firebase
        databaseAnuncios = FirebaseDatabase.getInstance().reference.child("Anuncios")
        databaseUsuarios = FirebaseDatabase.getInstance().reference.child("Usuarios")

        // Generar y mostrar la referencia aleatoria
        val referencia = findViewById<EditText>(R.id.RefAnun)
        referencia.setText(generarReferenciaAleatoria())
        referencia.isEnabled = false // Deshabilitar la edición del campo

        Btn_SiguienteAnunciarTrabajo.setOnClickListener {
            if (validarCampos()) {
                verificarCorreo()
            }
        }
        LinkPoliticasPriv.setOnClickListener {
            val url = "https://vedrunavall.cat"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun generarReferenciaAleatoria(): String {
        val random = Random()
        return String.format("%06d", random.nextInt(100000))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Esto hará que funcional al botón de atrás
        return true
    }

    private fun validarCampos(): Boolean {
        val titulo = tituloAnuncio.text.toString()
        val desc = descripcion.text.toString()
        val exp = experiencia.text.toString()
        val correoUsuario = correo.text.toString()
        val id = idUsuario.text.toString()
        val contactoUsuario = contacto.text.toString()
        val ciudadAnuncio = ciudad.text.toString()
        val precioAnuncio = precio.text.toString()
        val disponibilidadAnuncio = disponibilidad.text.toString()
        val aceptoPoliticas = checkBoxPoliticas.isChecked

        // Validar correo electrónico
        if (!correoUsuario.endsWith("@gmail.com")) {
            mostrarDialogo("Error", "El formato del correo electrónico introducido, no es correcto.")
            return false
        }

        // Validar número de contacto
        if (contactoUsuario.length != 9 || !contactoUsuario.matches("\\d+".toRegex())) {
            mostrarDialogo("Error", "El número de contacto debe tener almenos 9 números")
            return false
        }

        return if (titulo.isNotEmpty() && desc.isNotEmpty() && exp.isNotEmpty() && correoUsuario.isNotEmpty()
            && id.isNotEmpty() && contactoUsuario.isNotEmpty() && ciudadAnuncio.isNotEmpty()
            && precioAnuncio.isNotEmpty() && disponibilidadAnuncio.isNotEmpty() && aceptoPoliticas) {
            true
        } else {
            mostrarDialogo("Error", "Por favor completa todos los campos, para publicar el anuncio")
            false
        }
    }

    private fun verificarCorreo() {
        val correoUsuario = correo.text.toString()

        // Verificar si el correo electrónico existe en la base de datos
        databaseUsuarios.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var correoExistente = false
                for (usuarioSnapchot in snapshot.children) {
                    val correoDB = usuarioSnapchot.child("correo").value.toString()
                    if (correoDB.equals(correoUsuario, ignoreCase = true)) {
                        correoExistente = true
                        break
                    }
                }

                if (!correoExistente) {
                    mostrarDialogo("Error", "El correo electrónico introducido, no existe")
                } else {
                    guardarAnuncio()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mostrarDialogo("Error", "Error al acceder a la base de datos")
            }
        })
    }

    private fun formatoTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }


    private fun guardarAnuncio() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        if (usuarioActual == null) {
            // Si no hay usuario autenticado, muestra un diálogo de error.
            mostrarDialogo("Error de Autenticación", "Debe iniciar sesión para publicar un anuncio.")
            return
        }

        val referencia = findViewById<EditText>(R.id.RefAnun).text.toString()
        val titulo = tituloAnuncio.text.toString()
        val desc = descripcion.text.toString()
        val exp = experiencia.text.toString()
        val correoUsuario = correo.text.toString()
        val id = idUsuario.text.toString()
        val contactoUsuario = contacto.text.toString()
        val ciudadAnuncio = ciudad.text.toString()
        val precioAnuncio = try {
            precio.text.toString().toDouble()  // Intenta convertir el texto a Double
        } catch (e: NumberFormatException) {
            0.0  // Asigna un valor predeterminado en caso de error
        }
        val disponibilidadAnuncio = try {
            disponibilidad.text.toString().toInt()  // Intenta convertir el texto a Int
        } catch (e: NumberFormatException) {
            0  // Asigna un valor predeterminado en caso de error
        }
        val timestamp = System.currentTimeMillis()
        val Fecha = formatoTimestamp(timestamp)

        val anuncio = AnuncioBBDD(referencia, titulo, desc, exp, correoUsuario, id, contactoUsuario, ciudadAnuncio, precioAnuncio, disponibilidadAnuncio, timestamp)
        // Guarda el anuncio en la base de datos
        databaseAnuncios.push().setValue(anuncio)
            .addOnSuccessListener {
                mostrarDialogo("Éxito", "Tu anuncio se ha enviado, en cuenta se verifique, será publicado en la plataforma. Gracias por contar con nostros.") {
                    val intent = Intent(this@AnunciarTrabajo, Bienvenida::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                mostrarDialogo("Error", "Error al guardar el anuncio")
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
