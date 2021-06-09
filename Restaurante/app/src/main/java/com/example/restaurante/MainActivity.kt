package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.restaurante.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    var Bt1: Button? = null
    var Bt2: Button? = null
    var FAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTheme(R.style.Theme_Restaurante)

        FAuth = FirebaseAuth.getInstance()
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
            /*if (FAuth?.getCurrentUser() == null) {
                /*val intentoL = Intent(this, Login::class.java)
                startActivity(intentoL)*///otra forma de cargar otro activity
                startActivity(Intent(applicationContext, Login::class.java))
                finish()
            }else{
                /*val intentoM = Intent(this, Menu::class.java)
                startActivity(intentoM)*///otra forma de cargar otro activity
                startActivity(Intent(applicationContext, Login::class.java))
                finish()
            }*/
        }
        Bt2?.setOnClickListener {
            /*val intentoR = Intent(this, Registro::class.java)
            startActivity(intentoR)*///otra forma de cargar otro activity
            startActivity(Intent(applicationContext, Registro::class.java))
            //finish()
        }


    }
}