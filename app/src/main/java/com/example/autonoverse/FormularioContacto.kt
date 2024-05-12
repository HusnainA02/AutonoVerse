package com.example.autonoverse

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class FormularioContacto : AppCompatActivity() {

    private lateinit var btnEnviarSolicitud: Button
    private lateinit var btnAdjuntarCV: Button
    private lateinit var referenciaAnuncio: EditText
    private lateinit var tituloAnuncio: EditText
    private lateinit var idUsuarios: EditText
    private lateinit var disponibilidad: EditText
    private lateinit var correoUsuario: EditText
    private lateinit var numeroContacto: EditText
    private lateinit var Otros: EditText
    private lateinit var checkBoxPoliticas: CheckBox
    private lateinit var LinkPoliticasPriv: TextView
    private lateinit var databaseContactoAnuncios: DatabaseReference
    private lateinit var databaseAnuncios: DatabaseReference
    private lateinit var databaseRegistro: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var fileUri: Uri? = null
    private var isFileSelected = false

    companion object {
        private const val REQUEST_PICK_FILE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_contacto)

        // Configuración de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val referencia = intent.getStringExtra("Referencia")
        val titulo = intent.getStringExtra("Titulo")
        val idUsuario = intent.getStringExtra("IdUsuario")

        // Suponiendo que tienes TextViews para mostrar estos datos
        findViewById<TextView>(R.id.RefUsuTrab).text = referencia
        findViewById<TextView>(R.id.TituloTrab).text  = titulo
        findViewById<TextView>(R.id.IdUsuTrab).text = idUsuario

        btnEnviarSolicitud = findViewById(R.id.btnSoliTrab)
        btnAdjuntarCV = findViewById(R.id.btnCvTrab)
        referenciaAnuncio = findViewById(R.id.RefUsuTrab)
        tituloAnuncio = findViewById(R.id.TituloTrab)
        idUsuarios = findViewById(R.id.IdUsuTrab)
        disponibilidad = findViewById(R.id.DisponTrab)
        correoUsuario = findViewById(R.id.CorreoUsuForm)
        numeroContacto = findViewById(R.id.NumUsuForm)
        Otros = findViewById(R.id.OtrosForm)
        checkBoxPoliticas = findViewById(R.id.CheckBoxPoliticas)
        LinkPoliticasPriv = findViewById(R.id.LinkPoliticas)

        // Obtenemos la referencia de las bases de datos de Firebase
        databaseContactoAnuncios = FirebaseDatabase.getInstance().reference.child("ContactoAnuncios")
        databaseAnuncios = FirebaseDatabase.getInstance().reference.child("Anuncios")
        databaseRegistro = FirebaseDatabase.getInstance().reference.child("Usuarios")

        // Obtenemos la referencia al Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        btnAdjuntarCV.setOnClickListener {
            adjuntarArchivo()
        }

        btnEnviarSolicitud.setOnClickListener {
            if (validarCampos()) {
                verificarExistencia()
            }
        }
        LinkPoliticasPriv.setOnClickListener {
            val url = "https://vedrunavall.cat"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        btnEnviarSolicitud.setOnClickListener {
            if (validarCampos()) {
                mostrarDialogoConfirmacion()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Esto hará que funcional al botón de atrás
        return true
    }

    private fun adjuntarArchivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), REQUEST_PICK_FILE)
        } catch (e: ActivityNotFoundException) {
            mostrarDialogo("Error", "No hay aplicación para manejar archivos")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data?.data != null) {
            fileUri = data.data
            isFileSelected = true
            Toast.makeText(this, "Archivo seleccionado: ${fileUri?.path}", Toast.LENGTH_LONG).show()
        } else {
            isFileSelected = false
            Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validarCampos(): Boolean {
        val referencia = referenciaAnuncio.text.toString()
        val titulo = tituloAnuncio.text.toString()
        val id = idUsuarios.text.toString()
        val horasDisponibles = disponibilidad.text.toString()
        val correo = correoUsuario.text.toString()
        val numero = numeroContacto.text.toString()
        val otros = Otros.text.toString()
        val aceptoPoliticas = checkBoxPoliticas.isChecked

        // Validar que todos los campos estén llenos
        return if (referencia.isNotEmpty() && titulo.isNotEmpty() && id.isNotEmpty() &&
            horasDisponibles.isNotEmpty() && correo.isNotEmpty() && numero.isNotEmpty() && aceptoPoliticas && isFileSelected) {
            true
        } else {
            mostrarDialogo("Error", "Por favor completa todos los campos y asegúrate de haber adjuntado un archivo.")
            false
        }

    }


    private fun verificarExistencia() {
        val referencia = referenciaAnuncio.text.toString()
        val id = idUsuarios.text.toString()
        val correo = correoUsuario.text.toString()

        // Verificar si la referencia del anuncio existe en la base de datos de Anuncios
        databaseAnuncios.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var referenciaExiste = false
                for (anuncioSnapshot in snapshot.children) {
                    val referenciaDB = anuncioSnapshot.child("referencia").value.toString()
                    if (referenciaDB.equals(referencia, ignoreCase = true)) {
                        referenciaExiste = true
                        break
                    }
                }

                if (!referenciaExiste) {
                    mostrarDialogo("Error", "La referencia del anuncio no existe")
                } else {
                    // Verificar si la ID y el correo existen en la base de datos de Registro
                    verificarExistenciaRegistro(id, correo)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mostrarDialogo("Error", "Error al acceder a la base de datos de Anuncios")
            }
        })
    }

    private fun verificarExistenciaRegistro(id: String, correo: String) {
        databaseRegistro.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var idExiste = false
                var correoExiste = false
                for (registroSnapshot in snapshot.children) {
                    val idDB = registroSnapshot.child("ids").value.toString()
                    val correoDB = registroSnapshot.child("correo").value.toString()

                    if (idDB.equals(id, ignoreCase = true)) {
                        idExiste = true
                    }

                    if (correoDB.equals(correo, ignoreCase = true)) {
                        correoExiste = true
                    }

                    if (idExiste && correoExiste) {
                        // Si se encontraron coincidencias, guardar el contacto
                        guardarContacto()
                        return
                    }
                }

                // Si no se encontraron coincidencias para la ID o el correo, mostrar mensaje de error
                if (!idExiste) {
                    mostrarDialogo("Error", "La ID no existe en la base de datos de Registro")
                }
                if (!correoExiste) {
                    mostrarDialogo("Error", "El correo electrónico introducido, no existe.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mostrarDialogo("Error", "Error al acceder a la base de datos de Registro")
            }
        })
    }

    private fun guardarContacto() {
        val referencia = referenciaAnuncio.text.toString()
        val titulo = tituloAnuncio.text.toString()
        val id = idUsuarios.text.toString()
        val horasDisponibles = disponibilidad.text.toString()
        val correo = correoUsuario.text.toString()
        val numero = numeroContacto.text.toString()
        val otros = Otros.text.toString()
        val contactoId = databaseContactoAnuncios.push().key ?: ""

        // Si se selecciono un archivo, entonces procedemos a subirlo a Firebase
        if (fileUri != null) {
            val storageRef = storageReference.child("archivos/${UUID.randomUUID()}")

            // Subir el archivo a Firebase Storage
            storageRef.putFile(fileUri!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si la subida fue exitosa, obtenemos la URL del archivo
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val archivoUrl = uri.toString()

                            // Creamos un objeto ContactoBBDD con la URL del archivo
                            val contacto = ContactoBBDD(referencia, titulo, id, horasDisponibles, correo, numero, otros, archivoUrl)

                            // Guardamos el contacto en la base de datos ContactoAnuncios
                            databaseContactoAnuncios.child(contactoId).setValue(contacto)
                                .addOnSuccessListener {
                                    mostrarDialogo("Éxito", "Tu solicitud de contacto se ha enviado correctamente.")
                                    val intent = Intent(this@FormularioContacto, Bienvenida::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    mostrarDialogo("Error", "Error al enviar la solicitud de contacto")
                                }
                        }
                    } else {
                        mostrarDialogo("Error", "Error al subir el archivo")
                    }
                }
        } else {
            // Si no se selecciono ningún archivo, entonces guardamos el contacto sin URL de archivo
            val contacto = ContactoBBDD(referencia, titulo, id, horasDisponibles, correo, numero)

            // Guardamos el contacto en la base de datos ContactoAnuncios
            databaseContactoAnuncios.child(contactoId).setValue(contacto)
                .addOnSuccessListener {
                    mostrarDialogo("Éxito", "Tu solicitud de contacto se ha enviado correctamente.")
                    val intent = Intent(this@FormularioContacto, Bienvenida::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    mostrarDialogo("Error", "Error al enviar la solicitud de contacto")
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

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmación de privacidad")
            setMessage("¿Acepta que sus datos sean tratados por terceros con el fin de solicitar el trabajo?")
            setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                verificarExistencia()  // Continúa con la verificación solo si acepta
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this@FormularioContacto, "Solicitud cancelada", Toast.LENGTH_SHORT).show()
            }
            create()
            show()
        }
    }
}
