package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    var Bt1: Button? = null
    var Bt2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTheme(R.style.Theme_Restaurante)

        Bt1 = findViewById(R.id.Bt1)
        Bt2 = findViewById(R.id.Bt2)

        Bt1?.setOnClickListener {
            val user = Firebase.auth.currentUser
            if (user != null) {
                if (!user.isEmailVerified) {
                    user.sendEmailVerification()
                    Toast.makeText(this, "Comprueba tu correo para verificar la cuenta", Toast.LENGTH_LONG).show()
                }
                startActivity(Intent(applicationContext, Menu::class.java))
                Toast.makeText(this, "Sesion iniciada", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(applicationContext, Login::class.java))
            }
        }
        Bt2?.setOnClickListener {
            startActivity(Intent(applicationContext, Registro::class.java))
        }
    }
}