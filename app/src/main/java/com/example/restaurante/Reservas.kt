package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapter.ReservaAdapter
import com.example.restaurante.listener.ILoadReservas
import com.example.restaurante.model.ReferenciaReserva
import com.example.restaurante.model.Reserva
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_carrito.*
import kotlinx.android.synthetic.main.activity_reservas.*
import java.util.concurrent.atomic.AtomicInteger


class Reservas : AppCompatActivity(), ILoadReservas {
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null

    lateinit var reservaLoadListener: ILoadReservas

    var reservasList: MutableList<Reserva> = ArrayList()

    var referenciaReserva: ReferenciaReserva? = null
    var reserva: Reserva? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

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
                R.id.ContactoID -> startActivity(Intent(applicationContext, Contacto::class.java))
                R.id.CarritoID -> startActivity(Intent(applicationContext, Carrito::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }
        init()

        val user = Firebase.auth.currentUser

        FirebaseDatabase.getInstance().getReference("Reservas")
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(codigoTiemposhot: DataSnapshot, previousChildName: String?) {
                    for(reservaSnapShot in codigoTiemposhot.children){
                        if(reservaSnapShot.key != "numTotalReservas"){
                            reserva  = reservaSnapShot.getValue(Reserva::class.java)
                            if(reserva!!.ususario == user.uid){
                                reserva!!.key = reservaSnapShot.key
                                reservasList.add(reserva!!)
                            }
                        }
                    }
                    if(reservasList.size > 0){
                        reservaLoadListener.onReservaLoadSuccess(reservasList)
                    }else{
                        reservaLoadListener.onReservaLoadFailed("No has hecho ninguna reserva")
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    //TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    //TODO("Not yet implemented")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    reservaLoadListener.onReservaLoadFailed(databaseError.message)
                }
            });
    }

    private fun init() {
        reservaLoadListener = this

        recyclerReservas.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onReservaLoadSuccess(reservasList: MutableList<Reserva>?) {
        val adapter = ReservaAdapter(this, reservasList!!)
        recyclerReservas.adapter = adapter
    }

    override fun onReservaLoadFailed(message: String?) {
        Toast.makeText(this, message!!, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}