package com.example.restaurante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.restaurante.adapter.PlatoAdapter
import com.example.restaurante.listener.IMenuLoadListener
import com.example.restaurante.listener.IPlatoLoadListener
import com.example.restaurante.model.MenuModel
import com.example.restaurante.model.PlatoModel
import com.example.restaurante.utils.SpaceItemDecoration
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu_del_dia.*

class MenuDelDia : AppCompatActivity(), IPlatoLoadListener, IMenuLoadListener {
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null

    //variables para cargar el Menu
    lateinit var menuLoadListener: IMenuLoadListener
    lateinit var platoLoadListener: IPlatoLoadListener

    private val platoModelmenuList : MutableList<PlatoModel> = ArrayList()
    private var menuDelDia : MenuModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_del_dia)

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
                R.id.CartaID -> startActivity(Intent(applicationContext, Carta::class.java))
                R.id.MesasID -> startActivity(Intent(applicationContext, Mesas::class.java))
                R.id.ContactoID -> startActivity(Intent(applicationContext, Contacto::class.java))
                R.id.CarritoID -> startActivity(Intent(applicationContext, Carrito::class.java))
                R.id.TuPedidoID -> startActivity(Intent(applicationContext, TuPedido::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }
        init()
        loadMenuFromFirebase()
    }

    private fun loadMenuFromFirebase(){
        lateinit var database: DatabaseReference
        database = Firebase.database.reference

        database.child("InfoRestaurante").child("MenuDelDia").get().addOnSuccessListener {
            FirebaseDatabase.getInstance()
                .getReference("Menus")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            for (menuSnapShot in snapshot.children){
                                val menuModel  = menuSnapShot.getValue(MenuModel::class.java)
                                menuModel!!.key = menuSnapShot.key
                                if(menuModel.key == "${it.value}") {
                                    menuDelDia = menuModel
                                    break
                                }
                            }
                            menuLoadListener.onMenuLoadSuccess(menuDelDia)
                        }
                        else
                            menuLoadListener.onMenuLoadFailed("No hay un menu disponible")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        menuLoadListener.onMenuLoadFailed(error.message)
                    }
                })
        }.addOnFailureListener {
            Toast.makeText(this,"Se ha producido un error al recuperar el menu del día", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPlatosMenuFromFirebase(){
        FirebaseDatabase.getInstance()
            .getReference("Platos")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for (platoSnapShot in snapshot.children){
                            val platoModel  = platoSnapShot.getValue(PlatoModel::class.java)
                            platoModel!!.key = platoSnapShot.key
                            if(platoModel.key == menuDelDia!!.primerPlato1 || platoModel.key == menuDelDia!!.primerPlato2 ||
                                platoModel.key == menuDelDia!!.primerPlato3 ||platoModel.key == menuDelDia!!.primerPlato4 ||
                                platoModel.key == menuDelDia!!.segundoPlato1 || platoModel.key == menuDelDia!!.segundoPlato2 ||
                                platoModel.key == menuDelDia!!.segundoPlato3|| platoModel.key == menuDelDia!!.segundoPlato4) {
                                platoModelmenuList.add(platoModel)
                            }
                        }
                        platoLoadListener.onPlatosLoadSuccess(platoModelmenuList)
                    }
                    else
                        platoLoadListener.onPlatoLoadFailed("No hay platos disponibles")
                }
                override fun onCancelled(error: DatabaseError) {
                    platoLoadListener.onPlatoLoadFailed(error.message)
                }

            })
    }

    //funcion para cargar las bebidas
    private fun init() {
        //implementamos las funciones de las interfaces
        menuLoadListener = this
        platoLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerMenu.layoutManager = gridLayoutManager
        recyclerMenu.addItemDecoration(SpaceItemDecoration())
    }

    override fun onMenuLoadSuccess(menuModel: MenuModel?) {
        loadPlatosMenuFromFirebase()
    }

    override fun onMenuLoadFailed(message: String?) {
        Toast.makeText(this, message!!, Toast.LENGTH_LONG).show()
    }

    override fun onPlatoLoadSuccess(platoModel: PlatoModel?) {}

    override fun onPlatosLoadSuccess(platoModelList:List<PlatoModel>?) {
        val adapter = PlatoAdapter(this, platoModelList!!)
        recyclerMenu.adapter = adapter
    }

    override fun onPlatoLoadFailed(message: String?) {
        Toast.makeText(this, message!!, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}