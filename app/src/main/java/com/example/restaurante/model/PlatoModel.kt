package com.example.restaurante.model

import java.util.HashMap

class PlatoModel {
    var key: String? = null
    lateinit var alergenos: HashMap<String?, String?>
    var descripcion: String? = null
    var imagen: String? = null
    lateinit var ingredientesEstandar: HashMap<String?, String?>
    lateinit var ingredientesOpcionales: HashMap<String?, String?>
    var nombre: String? = null
    var plato: String? = null
    var precio: Float? = null
    var tipo: String? = null
}