package com.example.restaurante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Menu : AppCompatActivity() {
    //variables del menu hamburguesa
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null
    //variables de los botones del activity
    var btnMenuDia: Button? = null
    var btnCarta: Button? = null
    var btnMesas: Button? = null
    var btnDelivery: Button? = null
    var btnContacto: Button? = null
    var btnPerfil: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)

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
                R.id.TuPedidoID -> startActivity(Intent(applicationContext, TuPedido::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }

        btnMenuDia = findViewById(R.id.btnMenuDia)
        btnCarta = findViewById(R.id.btnCarta)
        btnMesas = findViewById(R.id.btnMesas)
        btnDelivery = findViewById(R.id.btnDelivery)
        btnContacto = findViewById(R.id.btnContacto)
        btnPerfil = findViewById(R.id.btnPerfil)

        btnMenuDia?.setOnClickListener {
            startActivity(Intent(applicationContext, MenuDelDia::class.java))
        }
        btnCarta?.setOnClickListener {
            startActivity(Intent(applicationContext, Bebidas::class.java))
        }
        btnMesas?.setOnClickListener {
            startActivity(Intent(applicationContext, Mesas::class.java))
        }
        btnDelivery?.setOnClickListener {
            startActivity(Intent(applicationContext, Bebidas::class.java))
        }
        btnContacto?.setOnClickListener {
            startActivity(Intent(applicationContext, Contacto::class.java))
        }
        btnPerfil?.setOnClickListener {
            startActivity(Intent(applicationContext, Perfil::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}