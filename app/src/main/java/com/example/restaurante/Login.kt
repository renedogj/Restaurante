package com.example.restaurante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.example.restaurante.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 120
    }

    var loginTv: TextView? = null
    var forgotPassword: TextView? = null
    var emailEt: EditText? = null
    var passwordEt: EditText? = null
    var iniciarBtn: Button? = null
    var salirBtn: Button? = null
    var progressBar: ProgressBar? = null
    var iniciarSesisonGoogle: Button? = null

    private var mAuth: FirebaseAuth? = null
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginTv = findViewById(R.id.login_user_tx1)
        forgotPassword = findViewById(R.id.login_user_tx2)
        emailEt = findViewById(R.id.login_user_et1)
        passwordEt = findViewById(R.id.login_user_et2)
        iniciarBtn = findViewById(R.id.login_user_bt1)
        salirBtn = findViewById(R.id.login_user_bt2)
        progressBar = findViewById(R.id.progressBar)
        iniciarSesisonGoogle = findViewById(R.id.iniciarSesisonGoogle)

        mAuth = FirebaseAuth.getInstance()

        loginTv?.setOnClickListener {
            startActivity(Intent(applicationContext, Registro::class.java))
            finish()
        }

        forgotPassword?.setOnClickListener {
            startActivity(Intent(applicationContext, ForgotPassword::class.java))
            finish()
        }

        iniciarBtn?.setOnClickListener {
            login()
        }

        salirBtn?.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        iniciarSesisonGoogle?.setOnClickListener {
            signIn()
        }
    }

    private fun login() {
        val email = emailEt!!.text.toString().trim { it <= ' ' }
        val password = passwordEt!!.text.toString().trim { it <= ' ' }

        if (email.isEmpty()) {
            emailEt!!.error = "Campo email requerido"
            emailEt!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt!!.error = "Introduce una direccion de correo valida"
            emailEt!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            passwordEt!!.error = "Campo password requerido"
            passwordEt!!.requestFocus()
            return
        }
        if (password.length < 6) {
            passwordEt!!.error = "La contraseña debe contener al menos 6 caracteres"
            passwordEt!!.requestFocus()
            return
        }

        progressBar!!.visibility = View.VISIBLE

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){

                    val user = FirebaseAuth.getInstance().currentUser

                    if(user.isEmailVerified){
                        startActivity(Intent(applicationContext, Menu::class.java))
                        Toast.makeText(this@Login, "Sesion iniciada", Toast.LENGTH_LONG).show()

                    }else{
                        user.sendEmailVerification()
                        Toast.makeText(this@Login, "Comprueba tu correo para verificar la cuenta", Toast.LENGTH_LONG).show()

                    }



                }else{
                    Toast.makeText(this@Login, "Error al iniciar sesion, comprueba tus credenciales", Toast.LENGTH_LONG).show()

                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SingInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SingInActivity", "Google sign in failed", e)
                }
            }else{
                Log.w("SingInActivity",exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SingInActivity", "signInWithCredential:success")
                    val user = mAuth?.currentUser
                    Toast.makeText(this,"Auntenticado con google",Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SingInActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}