package com.example.restaurante

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registro.*


class Registro : AppCompatActivity(){

    private val auth = FirebaseAuth.getInstance()
    private val basedeDatos = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        SalirBtn.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))
        }
        registrarse.setOnClickListener {
            progressBar.visibility= View.VISIBLE
            val nombre = nombre_texto.text.toString().trim()
            val email = email_texto.text.toString().trim()
            val usuarioId = nickname_texto.text.toString().trim()
            val contrasena = contraseña_texto.text.toString().trim()
            //Como un switch
            when {
                TextUtils.isEmpty(nombre) -> {
                    nombre_texto.setError("Introduzca su nombre")
                    nombre_texto.requestFocus()
                    return@setOnClickListener
                }
                TextUtils.isEmpty(usuarioId) -> {
                    nickname_texto.setError("Introduzca su nombre de usuario")
                    nickname_texto.requestFocus()
                    return@setOnClickListener
                }
                TextUtils.isEmpty(email) -> {
                    email_texto.setError("Introduzca su email")
                    email_texto.requestFocus()
                    return@setOnClickListener
                }
                TextUtils.isEmpty(contrasena) -> {
                    contraseña_texto.setError("Introduzca su contraseña")
                    contraseña_texto.requestFocus()
                    return@setOnClickListener
                }
            }
            basedeDatos.collection("Users").document(usuarioId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        progressBar.visibility= View.INVISIBLE
                        AlertDialog.Builder(this).apply {
                            setTitle("Nombre usuario en uso")
                            setMessage("Introduzca uno nuevo")
                        }.show()
                        nickname_texto.requestFocus()
                        nickname_texto.setError("Inserte otro nombre de usuario")
                    } else {
                        //Si todos los campos son rellenados correctamente
                        auth.createUserWithEmailAndPassword(email, contrasena)
                            .addOnCompleteListener {
                                if (!it.isSuccessful) {
                                    progressBar.visibility= View.INVISIBLE
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Registro Fallido, correo en uso")
                                        setMessage("Por favor, inténtalo de nuevo")
                                    }.show()
                                }
                            }
                            .addOnSuccessListener {
                                //Meto la foto directamente en la variable
                                val foto =
                                    "https://firebasestorage.googleapis.com/v0/b/restaurante-358d8.appspot.com/o/ImagenesPerfil%2FManuxo%2FManuxo?alt=media&token=afbf0881-2f77-4da4-84ae-942e040a4cd9"
                                //Creo el objeto que voy a enviar a la base de datos
                                val usuario: Usuario =
                                    Usuario(nombre, null, email, usuarioId, contrasena, foto,"Hamburguesa es mi segundo nombre")
                                //Creo la coleccion que va a haber en la base de datos que se va a llamar usuarios , un documento que su id va a ser el nickname, y el objeto usuario lo meto a la base de datos
                                basedeDatos.collection("Users").document(usuarioId).set(usuario)
                                //Aqui lo que hago es si esta bien, el registro, (tuve un problema que no me cogia el displayName del que esta iniciado sesion)
                                //Actualizo el perfil y le doy valor al displayName ya que me daba valor null
                                val cambiarNick = userProfileChangeRequest {
                                    displayName = usuarioId
                                    photoUri = Uri.parse(foto)
                                }
                                progressBar.visibility= View.INVISIBLE
                                auth.currentUser.updateProfile(cambiarNick)
                                AlertDialog.Builder(this).apply {
                                    setTitle("Cuenta Creada")
                                    setMessage("Se ha registrado correctamente")
                                        //finish()
                                    }.show()
                                }
                            }
                    }
                }
        }
    }


