package com.example.restaurante

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.restaurante.adapter.IngredienteAdapter
import com.example.restaurante.listener.IPlatoLoadListener
import com.example.restaurante.model.PlatoModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_info_platos_menu.*

class InfoPlatosMenu : AppCompatActivity(), IPlatoLoadListener {
    //variables del menu hamburguesa
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null

    lateinit var platoLoadListener: IPlatoLoadListener

    private var ingredientesList: MutableList<String> = ArrayList()
    private var ingredientesOpcionalesList: MutableList<String> = ArrayList()
    private var alergenosList: MutableList<String> = ArrayList()

    private var imagenPlato: ImageView? = null
    private var tvNombrePlato: TextView? = null
    private var tvDescripcionPlato: TextView? = null
    private var tvIngredientes: TextView? = null
    private var tvIngredientesOpcionales: TextView? = null
    private var tvAlergenos: TextView? = null
    private var tvTipo: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_platos_menu)

        navView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawerLayout)

        imagenPlato = findViewById(R.id.imagenPlato)
        tvNombrePlato = findViewById(R.id.tvNombrePlato)
        tvDescripcionPlato = findViewById(R.id.tvDescripcionPlato)
        tvIngredientes = findViewById(R.id.tvIngredientes)
        tvIngredientesOpcionales = findViewById(R.id.tvIngredientesOpcionales)
        tvAlergenos = findViewById(R.id.tvAlergenos)
        tvTipo = findViewById(R.id.tvTipo)

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
        var keyPlato = intent.getStringExtra("keyPlato")
        init()
        loadPlatoFromFirebase(keyPlato)
    }

    private fun loadPlatoFromFirebase(keyPlato: String?){
        FirebaseDatabase.getInstance()
            .getReference("Platos").child(keyPlato!!)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(platoSnapShot: DataSnapshot) {
                    if(platoSnapShot.exists()){
                        var platoModel  = platoSnapShot.getValue(PlatoModel::class.java)
                        platoLoadListener.onPlatoLoadSuccess(platoModel)
                    }
                    else
                        platoLoadListener.onPlatoLoadFailed("Plato no disponible")
                }
                override fun onCancelled(error: DatabaseError) {
                    platoLoadListener.onPlatoLoadFailed(error.message)
                }

            })
    }

    private fun init() {
        //implementamos las funciones de las interfaces
        platoLoadListener = this
    }

    override fun onPlatoLoadSuccess(platoModel: PlatoModel?) {
        Glide.with(this)
            .load(platoModel!!.imagen)
            .into(imagenPlato!!)
        tvNombrePlato!!.text = platoModel.nombre
        tvDescripcionPlato!!.text = platoModel.descripcion

        if(platoModel.tipo == "vegetariano"){
            tvTipo!!.text = "Vegetariano"
            cardTipo.visibility = View.VISIBLE
        }else if(platoModel.tipo == "vegano"){
            tvTipo!!.text = "Vegano"
            cardTipo.visibility = View.VISIBLE
        }

        if(platoModel.ingredientes != null){
            tvIngredientes!!.text = "Ingredientes"
            for (ingrediente in platoModel.ingredientes!!){
                ingredientesList.add(ingrediente.value!!)
            }
            recyclerIngredientes.layoutManager = GridLayoutManager(this, ingredientesList.size)
            var adapterIngredientes = IngredienteAdapter(this, ingredientesList)
            recyclerIngredientes.adapter = adapterIngredientes
        }

        if(platoModel.ingredientesOpcionales != null){
            tvIngredientesOpcionales!!.text = "Ingredientes opcionales"
            for (ingredienteOpcional in platoModel.ingredientesOpcionales!!){
                ingredientesOpcionalesList.add(ingredienteOpcional.value!!)
            }
            recyclerIngredientesOpcionales.layoutManager = GridLayoutManager(this, ingredientesOpcionalesList.size)
            var adapterIngredientesOpcionales = IngredienteAdapter(this, ingredientesOpcionalesList)
            recyclerIngredientesOpcionales.adapter = adapterIngredientesOpcionales
        }

        if(platoModel.alergenos != null){
            tvAlergenos!!.text = "Alergenos"
            for (alergeno in platoModel.alergenos!!){
                alergenosList.add(alergeno.value!!)
            }
            recyclerAlergenos.layoutManager = GridLayoutManager(this, alergenosList.size)
            var adapterAlergenos = IngredienteAdapter(this, alergenosList,R.color.naranja1)
            recyclerAlergenos.adapter = adapterAlergenos
        }
    }

    override fun onPlatosLoadSuccess(platoModelList:List<PlatoModel>?) {}

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