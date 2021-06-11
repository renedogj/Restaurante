package com.example.restaurante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.restaurante.Interface.IFireStoreLoadDone
import com.example.restaurante.model.Movie
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_stories.*
import java.lang.Exception

class Stories : AppCompatActivity(), IFireStoreLoadDone {
    //variables del menu hamburguesa
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null
    //Stories
    internal var counter = 0

    lateinit var movieRef:CollectionReference
    lateinit var firebaseLoadDone: IFireStoreLoadDone


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stories)
        //Menu Hamburguesa
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
                R.id.StoriesID -> startActivity(Intent(applicationContext, Stories::class.java))
                R.id.ContactoID -> startActivity(Intent(applicationContext, Contacto::class.java))
                R.id.CarritoID -> startActivity(Intent(applicationContext, Carrito::class.java))
                R.id.SalirID -> startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            true
        }

        //Init
        movieRef = FirebaseFirestore.getInstance().collection("Movies")
        firebaseLoadDone = this

        //Event
        btn_reverse.setOnClickListener { stories.reverse() }
        btn_resume.setOnClickListener { stories.resume() }
        btn_pause.setOnClickListener { stories.pause() }
        btn_load.setOnClickListener {
            movieRef.get()
                .addOnFailureListener { e -> firebaseLoadDone.onFirebaseLoadFailed(e.message!!) }
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val movies = ArrayList<Movie>()
                        for(movieSnapshot in task.result!!)
                        {
                            val movie = movieSnapshot.toObject<Movie>(Movie::class.java)
                            movies.add(movie)
                        }
                        firebaseLoadDone.onFirebaseLoadSuccess(movies)

                    }
                }
        }
    }

    override fun onFirebaseLoadSuccess(movieList: List<Movie>) {
        stories.setStoriesCount(movieList.size)
        stories.setStoryDuration(3000L)
        //Load first image
        Picasso.get().load(movieList[0].image).into(image, object: Callback {
            override fun onSuccess() {
                //if first image is loaded - start stories
                stories.startStories()
            }

            override fun onError(e: Exception?) {}
        })

        stories.setStoriesListener(object :StoriesProgressView.StoriesListener{
            override fun onNext() {
                if(counter < movieList.size){
                    counter ++
                    Picasso.get().load(movieList[counter].image).into(image)
                }
            }

            override fun onPrev() {
                if(counter > 0){
                    counter --
                    Picasso.get().load(movieList[counter].image).into(image)
                }
            }

            override fun onComplete() {
                counter = 0//reset
                Toast.makeText(this@Stories, "Carga completada", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onFirebaseLoadFailed(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}