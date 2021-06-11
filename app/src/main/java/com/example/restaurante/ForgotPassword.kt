package com.example.restaurante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private var emailEditText: EditText? = null
    private var resetPasswordButton: Button? = null
    private var salirbtn: Button? = null

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.email)
        resetPasswordButton = findViewById(R.id.resetPassword)
        salirbtn = findViewById(R.id.salirr)

        auth = FirebaseAuth.getInstance()

        salirbtn?.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))

        }
        resetPasswordButton?.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        val email = emailEditText!!.text.toString().trim(){ it <= ' ' }

        if (email.isEmpty()) {
            emailEditText!!.error = "Campo email requerido"
            emailEditText!!.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText!!.error = "Introduce una direccion de correo valida"
            emailEditText!!.requestFocus()
            return
        }

        auth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this@ForgotPassword, "Comprueba tu email para resetear la contraseña", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@ForgotPassword, "Intentalo de nuevo, ha ocurrido un error", Toast.LENGTH_LONG).show()
            }
        }
    }
}