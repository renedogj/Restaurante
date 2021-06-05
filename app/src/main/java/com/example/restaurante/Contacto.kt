package com.example.restaurante

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Contacto : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null
    var tvTelefonoNumero: TextView? = null
    var tvDireccion: TextView? = null
    var bttnComoLlegar: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto)

        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)
        tvTelefonoNumero = findViewById(R.id.tvTelefonoNumero)
        tvDireccion = findViewById(R.id.tvDireccion)
        bttnComoLlegar = findViewById(R.id.bttnComoLlegar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)//los string son para que al cerrar o abrir el drawerLayout produzca sonido (funcionalidad para ciegos)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)//para abrir el menu hamburguesa(toggle button) y cuando esta abierto y clickamos en la flecha de ir atras se cerrara de nuevo

        navView?.setNavigationItemSelectedListener {//Segun el item seleccionado hacer lo siguiente...
            when(it.itemId) {
                R.id.PerfilID -> startActivity(Intent(applicationContext, Perfil::class.java))
                R.id.MenuPrincipalID -> startActivity(Intent(applicationContext, Menu::class.java))
                R.id.MenuDiaID -> startActivity(Intent(applicationContext, MenuDelDia::class.java))
                R.id.CartaID -> startActivity(Intent(applicationContext, Bebidas::class.java))
                R.id.MesasID -> startActivity(Intent(applicationContext, Mesas::class.java))
                R.id.ContactoID -> startActivity(Intent(applicationContext, Contacto::class.java))
                R.id.CarritoID -> startActivity(Intent(applicationContext, Carrito::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FragmentUbicacionRestaurante>(R.id.fragment_ubicacion_restaurante)
            }
        }

        lateinit var database: DatabaseReference
        database = Firebase.database.reference

        database.child("InfoRestaurante").child("Telefono").get().addOnSuccessListener {
            var numTelefono = "${it.value}"
            tvTelefonoNumero!!.text = numTelefono
            tvTelefonoNumero!!.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numTelefono")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al mostrar el telefono", Toast.LENGTH_SHORT).show()
            tvTelefonoNumero!!.text = "Error al mostrar el telefono"
        }

        database.child("InfoRestaurante").child("Ubicacion").get().addOnSuccessListener {
            var ubicacionRestauranteString = "${it.value}"
            tvDireccion!!.text = ubicacionRestauranteString
            bttnComoLlegar?.setOnClickListener {
                abrirMaps(Uri.parse("geo:0,0?q=" + Uri.encode(ubicacionRestauranteString)))
            }
            tvDireccion?.setOnClickListener {
                abrirMaps(Uri.parse("geo:0,0?q=" + Uri.encode(ubicacionRestauranteString)))
            }

        }.addOnFailureListener{
            Toast.makeText(this, "Error al mostrar la direccion", Toast.LENGTH_SHORT).show()
            tvDireccion!!.text = "Error al mostrar la direccion"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun abrirMaps(geoLocation: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = geoLocation
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}