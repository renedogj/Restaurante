package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class Registro : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null

    private var etNombre: EditText? = null
    private var etEdad: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var progressBar: ProgressBar? = null
    private var AceptarBtn: Button? = null
    private var SalirBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        mAuth = FirebaseAuth.getInstance()

        AceptarBtn = findViewById<View>(R.id.AceptarBtn) as Button
        SalirBtn = findViewById<View>(R.id.SalirBtn) as Button
        AceptarBtn!!.setOnClickListener(this)
        etNombre = findViewById<View>(R.id.etNombre) as EditText
        etEdad = findViewById<View>(R.id.etEdad) as EditText
        etEmail = findViewById<View>(R.id.etEmail) as EditText
        etPassword = findViewById<View>(R.id.etPassword) as EditText
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar

        SalirBtn?.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.AceptarBtn -> aceptarbtn()
        }
    }

    private fun aceptarbtn() {
        val email = etEmail!!.text.toString().trim { it <= ' ' }
        val password = etPassword!!.text.toString().trim { it <= ' ' }
        val nombre = etNombre!!.text.toString().trim { it <= ' ' }
        val edad = etEdad!!.text.toString().trim { it <= ' ' }
        if (nombre.isEmpty()) {
            etNombre!!.error = "Campo nombre requerido"
            etNombre!!.requestFocus()
            return
        }
        if (edad.isEmpty()) {
            etEdad!!.error = "Campo edad requerido"
            etEdad!!.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etEmail!!.error = "Campo email requerido"
            etEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword!!.error = "Campo password requerido"
            etPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword!!.error = "La contraseña debe contener al menos 6 caracteres"
            etPassword!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail!!.error = "Introduce una direccion de correo valida"
            etEmail!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = Usuario(nombre, edad, email)
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser.uid)
                        .setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@Registro,
                                    "El usuario ha sido registrado correctamente",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar!!.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this@Registro,
                                    "Error al registrar usuario, intentelo de nuevo",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar!!.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(
                        this@Registro,
                        "Error al registrar usuario, intentelo de nuevo",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }
}
