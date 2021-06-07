package com.example.restaurante

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlin.coroutines.Continuation

import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_perfil.*

class Perfil : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null

    //Variables perfil
    //Codigo el que queramos
    private val file = 1

    //Raiz del Storage
    private val storageReferencia = Firebase.storage.getReference("ImagenesPerfil")

    //Selecciono el Uri de la imagen que va a elegir el usuario
    private var imagenUri: Uri? = null
    private var contador = 1
    private val baseDatos = Firebase.firestore
    private val autentificacion = Firebase.auth
    private val coleccionUsuarios = baseDatos.collection("Users")
    private var campoNombreTextoPerfil = ""
    private var campoApellidosTextoPerfil = ""
    private var campoCorreoTextoPerfil = ""
    private var campoNombreIdTextoPerfil = ""
    private var campoContrasenaPerfil = ""
    private var campoDescripcionTextoPerfil=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        //Parte menu Hamburguesa
        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open,
            R.string.close
        )//los string son para que al cerrar o abrir el drawerLayout produzca sonido (funcionalidad para ciegos)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)//para abrir el menu hamburguesa(toggle button) y cuando esta abierto y clickamos en la flecha de ir atras se cerrara de nuevo

        navView?.setNavigationItemSelectedListener {//Segun el item seleccionado hacer lo siguiente...
            when (it.itemId) {
                R.id.PerfilID -> startActivity(Intent(applicationContext, Perfil::class.java))
                R.id.MenuPrincipalID -> startActivity(Intent(applicationContext, Menu::class.java))
                R.id.MenuDiaID -> startActivity(Intent(applicationContext, MenuDelDia::class.java))
                R.id.CartaID -> startActivity(Intent(applicationContext, Bebidas::class.java))
                R.id.MesasID -> startActivity(Intent(applicationContext, Mesas::class.java))
                R.id.StoriesID -> startActivity(Intent(applicationContext, Stories::class.java))
                R.id.ContactoID -> startActivity(Intent(applicationContext, Contacto::class.java))
                R.id.CarritoID -> startActivity(Intent(applicationContext, Carrito::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }
        //Parte perfil

        tuReserva_btn.setOnClickListener {
            startActivity(Intent(applicationContext, Reservas::class.java))
        }

        progressBar_cp.visibility=View.VISIBLE

        //Enseñar Campos
        coleccionUsuarios.document(autentificacion.currentUser.displayName).get()
            .addOnSuccessListener {
                //Rellenar campos
                nombre_ImagenPerfil.text = it.getString("nombreUsuario").toString()
                nombrePerfil_texto.setText(it.getString("usuarioId").toString())
                apellidosPerfil_texto.setText(it.getString("apellidos").toString())
                correoPerfil_texto.setText(it.getString("correo").toString())
                nicknamePerfil_texto.setText(it.getString("usuarioId").toString())
                contrasenaPerfil_texto.setText(it.getString("contrasena").toString())
                descripcion_texto.setText(it.getString("descripcion").toString())
                if (apellidosPerfil_texto.text.toString() == "null") {
                    apellidosPerfil_texto.setText("")
                }
                if (descripcion_texto.text.toString() == "null") {
                    descripcion_texto.setText("")
                }

                var urlImagen = it.getString("imagen").toString()
                Glide.with(this)
                    .load(urlImagen)
                    .fitCenter()
                    .into(imagenPerfil)
                progressBar_cp.visibility= View.INVISIBLE
            }
        //Para q este desactivado tiene q ser el desactivar false
        editarPerfil_btn.setOnClickListener {
            if (contador % 2 != 0) {
                imagenPerfil.isClickable = true
                imagenPerfil.setOnClickListener {
                    seleccionarFotoGaleria()
                }
                camposPerfil(true)
                editarPerfil_btn.setText("Guardar Cambios")
            } else {
                progressBar_cp.visibility= View.VISIBLE
                imagenPerfil.isClickable = false
                //Coger los campos
                campoNombreTextoPerfil = nombrePerfil_texto.text.toString()
                campoApellidosTextoPerfil = apellidosPerfil_texto.text.toString()
                campoCorreoTextoPerfil = correoPerfil_texto.text.toString()
                campoNombreIdTextoPerfil = nicknamePerfil_texto.text.toString()
                campoContrasenaPerfil = contrasenaPerfil_texto.text.toString()
                campoDescripcionTextoPerfil=descripcion_texto.text.toString()

                //Validar Campos
                when {
                    TextUtils.isEmpty(campoNombreTextoPerfil) -> {
                        nombrePerfil_texto.setError("Introduzca su nombre")
                        nombrePerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    TextUtils.isEmpty(campoApellidosTextoPerfil) -> {
                        apellidosPerfil_texto.setError("Introduzca sus apellidos")
                        apellidosPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    TextUtils.isEmpty(campoCorreoTextoPerfil) -> {
                        correoPerfil_texto.setError("Introduzca su email")
                        correoPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    TextUtils.isEmpty(campoNombreIdTextoPerfil) -> {
                        nicknamePerfil_texto.setError("Introduzca su nickname")
                        nicknamePerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    TextUtils.isEmpty(campoContrasenaPerfil) -> {
                        contrasenaPerfil_texto.setError("Introduzca su contraseña")
                        contrasenaPerfil_texto.requestFocus()
                        return@setOnClickListener
                    }
                    TextUtils.isEmpty(campoDescripcionTextoPerfil) ->{
                        descripcion_texto.setError("Introduzca su descripción")
                        descripcion_texto.requestFocus()
                        return@setOnClickListener
                    }
                }
                //Si esta bien actualiza los campos
                actualizarRegistros(
                    campoNombreTextoPerfil,
                    campoApellidosTextoPerfil,
                    campoCorreoTextoPerfil,
                    campoNombreIdTextoPerfil,
                    campoContrasenaPerfil,
                    campoDescripcionTextoPerfil
                )
                camposPerfil(false)
                editarPerfil_btn.setText("Editar Perfil")
            }
            contador++
        }
    }
    //Parte perfil
    private fun camposPerfil(desactivador: Boolean) {
        nombrePerfil_texto.isEnabled = desactivador
        apellidosPerfil_texto.isEnabled = desactivador
        descripcion_texto.isEnabled=desactivador
    }

    private fun actualizarRegistros(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String,
        descripcion: String
    ) {
        var foto = ""
        //Lo subimos al Storage
        if (imagenUri == null) {
            foto = autentificacion.currentUser.photoUrl.toString()
            actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto,descripcion)
        } else {
            //La primera vez va a fallar en eliminar la foto ya que no existe esa referencia, entonces ira al addOnFailureListener
            storageReferencia.child("$usuarioId/" + autentificacion.currentUser.displayName.toString())
                .delete()
                .addOnSuccessListener {
                    crearReferenciaSustituir(usuarioId, foto, nombre, apellidos, correo, contrasena,descripcion)
                }
                .addOnFailureListener {
                    crearReferenciaSustituir(usuarioId, foto, nombre, apellidos, correo, contrasena,descripcion)
                }
        }
    }

    //Este metodo  es para que no se acumulen las fotos en el storage a la hora que se cambie la foto de perfil
    private fun crearReferenciaSustituir(
        usuarioId: String,
        foto: String,
        nombre: String,
        apellidos: String,
        correo: String,
        contrasena: String,
        descripcion: String
    ) {
        //Aqui creo la subcarpeta de la referencia
        var foto1 = foto
        val folder =
            storageReferencia.child("$usuarioId/" + autentificacion.currentUser.displayName.toString())
        folder.putFile(imagenUri!!).addOnSuccessListener {
            //Si se sube bien al Storage, Creo el link con downloadURL
            folder.downloadUrl.addOnSuccessListener { urlImagen ->
                foto1 = urlImagen.toString()
                actualizarPerfil(nombre, apellidos, correo, usuarioId, contrasena, foto1,descripcion)
            }
        }
    }

    private fun actualizarPerfil(
        nombre: String,
        apellidos: String,
        correo: String,
        usuarioId: String,
        contrasena: String,
        foto: String,
        descripcion: String
    ) {
        //Meterlos en un objeto
        val usuario: Usuario = Usuario(
            nombre,
            apellidos,
            correo,
            usuarioId,
            contrasena,
            foto,
            descripcion
        )

        nombre_ImagenPerfil.setText(autentificacion.currentUser.displayName)
        baseDatos.collection("Users").document(campoNombreIdTextoPerfil).set(usuario)
            .addOnSuccessListener {
                val cambiarNick = userProfileChangeRequest {
                    displayName = usuarioId
                    photoUri = Uri.parse(foto)
                }
                autentificacion.currentUser.updateProfile(cambiarNick)
                progressBar_cp.visibility= View.INVISIBLE
                Toast.makeText(this, "Perfil Actualizado Correctamente", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seleccionarFotoGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        //Que seleccione solo imagenes de todas las extensiones
        intent.type = "image/*"
        startActivityForResult(intent, file)
    }

    //Sobreescribo el metodo para sobreescribir el valor de la Uri de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == file && resultCode == Activity.RESULT_OK -> {
                imagenUri = data!!.data
                imagenPerfil.setImageURI(imagenUri)
            }
        }
    }

    //Parte Menu Hamburguesa
    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}