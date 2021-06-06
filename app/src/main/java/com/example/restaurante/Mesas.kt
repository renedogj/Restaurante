package com.example.restaurante

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.restaurante.model.MenuModel
import com.example.restaurante.model.Reserva
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mesas.*
import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*


class Mesas : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle//boton de hamburguesa
    var drawerLayout: DrawerLayout? = null
    var navView: NavigationView? = null

    var numberPicker: NumberPicker? = null
    var tvPickDate: TextView? = null
    var tvPickTime: TextView? = null
    var bttonReservar: Button? = null

    private var dia: Int? = null
    private var mes: Int? = null
    private var anno: Int? = null
    private var hora: Int? = null
    private var minuto: Int? = null
    private var nombre: String? = null
    private var aforo: Long? = null
    private var numPersonas: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)

        navView = findViewById(R.id.navView)
        numberPicker = findViewById(R.id.numberPicker)
        tvPickDate = findViewById(R.id.tvPickDate)
        tvPickTime = findViewById(R.id.tvPickTime)
        bttonReservar = findViewById(R.id.bttonReservar)

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

        numberPicker!!.minValue = 1
        numberPicker!!.maxValue = 10

        ponerFechaHoraActual()

        tvPickDate!!.setOnClickListener {
            val c = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    var cadena: String? = ""
                    dia = dayOfMonth
                    if (dia!! < 10) {
                        cadena += "0$dia"
                    } else {
                        cadena += "$dia"
                    }
                    mes = (monthOfYear + 1)
                    if (mes!! < 10) {
                        cadena += "/0$mes"
                    } else {
                        cadena += "/$mes"
                    }
                    anno = year
                    tvPickDate!!.text = "$cadena/$anno"
                },
                c[Calendar.YEAR],
                c[Calendar.MONTH],
                c[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
        }

        tvPickTime!!.setOnClickListener {
            val c = Calendar.getInstance()

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    var cadena: String? = ""
                    hora = hourOfDay
                    if (hora!! < 10) {
                        cadena += "0$hora"
                    } else {
                        cadena += "$hora"
                    }
                    minuto = minute
                    if (minuto!! < 10) {
                        cadena += ":0$minuto"
                    } else {
                        cadena += ":$minuto"
                    }
                    tvPickTime!!.text = "$cadena"
                },
                c[Calendar.HOUR_OF_DAY],
                c[Calendar.MINUTE],
                true
            )
            timePickerDialog.show()
        }

        bttonReservar!!.setOnClickListener {
            var cadenaFecha: String?
            nombre = etNombre?.text.toString()
            if (nombre == "") {
                Toast.makeText(
                    this,
                    "Es necesario introducir un nombre para hacer la reserva",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!comprobarFechaHora()) {
                Toast.makeText(this, "Introduce una fecha valida", Toast.LENGTH_SHORT).show()
            } else if (hora!! < 11 || hora!! >= 23) {
                Toast.makeText(
                    this,
                    "No aceptamos reservas antes de las 12h ni después de las 23h",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cadenaFecha = "$anno"
                if (mes!! < 10) {
                    cadenaFecha += "0$mes"
                } else {
                    cadenaFecha += "$mes"
                }
                if (dia!! < 10) {
                    cadenaFecha += "0$dia"
                } else {
                    cadenaFecha += "$dia"
                }
                if (hora!! >= 12 && hora!! < 18) {
                    cadenaFecha += "01"
                } else if (hora!! >= 18 && hora!! < 23) {
                    cadenaFecha += "02"
                }

                lateinit var database: DatabaseReference
                database = Firebase.database.reference

                database.child("Reservas").child(cadenaFecha).child("numTotalReservas").get()
                    .addOnSuccessListener {
                        if(it.exists()){
                            numPersonas = it.value as Long?
                        }else{
                            numPersonas = 0
                        }
                        database.child("InfoRestaurante").child("Aforo").get()
                            .addOnSuccessListener {
                                if(it.exists()){
                                    aforo = it.value as Long?
                                }else{
                                    aforo = 0
                                }
                                val numPersonasFranjaReserva = (numPersonas!! + numberPicker!!.value)
                                if(numPersonasFranjaReserva <= aforo!!){
                                    guardarReserva(cadenaFecha,numPersonasFranjaReserva)
                                    Toast.makeText(this,"Reserva realizada con éxito a nombre de: $nombre",Toast.LENGTH_LONG).show()
                                    ponerFechaHoraActual()
                                    etNombre.setText("")
                                    numberPicker!!.value = 1
                                }else{
                                    Toast.makeText(this,"No se dispone de suficiente aforo para una reserva de ${numberPicker!!.value} personas",Toast.LENGTH_LONG).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this,"Se ha producido un error al recuperar el aforo del restaurante", Toast.LENGTH_SHORT).show()
                                aforo = 0
                            }
                    }.addOnFailureListener {
                        Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show()
                        numPersonas = 0
                    }
            }
        }
    }

    private fun guardarReserva(codigoTiempo: String?, numPersonasFranjaReserva: Long?) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val pushedPostRef = FirebaseDatabase.getInstance().getReference("Reservas")
                .child(codigoTiempo!!).push()
            pushedPostRef.setValue(Reserva(dia,mes,anno,hora, minuto, nombre, numberPicker!!.value.toLong(), user.uid))

            //FirebaseDatabase.getInstance().getReference("Reservas").child(codigoTiempo!!).push()
            val reservaID = pushedPostRef.key
            FirebaseDatabase.getInstance().getReference("Users").child(user.uid).child("Reservas")
                .child(codigoTiempo).child(reservaID!!).setValue(reservaID)
        } else {
            FirebaseDatabase.getInstance().getReference("Reservas")
                .child(codigoTiempo!!).push()
                .setValue(Reserva(hora, minuto, nombre, numberPicker!!.value.toLong()))
        }
        Firebase.database.reference.child("Reservas").child(codigoTiempo)
            .child("numTotalReservas").setValue(numPersonasFranjaReserva)
    }

    fun comprobarFechaHora(): Boolean {
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH) + 1
        val year = c.get(Calendar.YEAR)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        if (anno!! > year) {
            return true
        } else if (anno!! == year) {
            if (mes!! > month) {
                return true
            } else if (mes!! == month) {
                if (dia!! > day) {
                    return true
                } else if (dia!! == day) {
                    if (hora!! > hour) {
                        return true
                    } else if (hora!! == hour) {
                        return minuto!! > minute
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }

    fun ponerFechaHoraActual() {
        val c = Calendar.getInstance()
        dia = c.get(Calendar.DAY_OF_MONTH)
        mes = c.get(Calendar.MONTH) + 1
        anno = c.get(Calendar.YEAR)
        hora = c.get(Calendar.HOUR_OF_DAY)
        minuto = c.get(Calendar.MINUTE)
        var cadenaFecha: String? = ""
        if (dia!! < 10) {
            cadenaFecha += "0$dia"
        } else {
            cadenaFecha += "$dia"
        }
        if (mes!! < 10) {
            cadenaFecha += "/0$mes"
        } else {
            cadenaFecha += "/$mes"
        }
        tvPickDate!!.text = "$cadenaFecha/$anno"
        var cadenaHora: String? = ""
        if (hora!! < 10) {
            cadenaHora += "0$hora"
        } else {
            cadenaHora += "$hora"
        }
        if (minuto!! < 10) {
            cadenaHora += ":0$minuto"
        } else {
            cadenaHora += ":$minuto"
        }
        tvPickTime!!.text = "$cadenaHora"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//para que funcionen los clicks del menu hamburguesa
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}