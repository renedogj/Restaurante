package com.example.restaurante.model

import java.util.HashMap

class PlatoModel {
    var key: String? = null
    var alergenos: HashMap<String?, String?>? = null
    var descripcion: String? = null
    var imagen: String? = null
    var ingredientes: HashMap<String?, String?>? = null
    var ingredientesOpcionales: HashMap<String?, String?>? = null
    var nombre: String? = null
    var plato: String? = null
    var tipo: String? = null
}