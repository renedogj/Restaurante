package com.example.restaurante

import java.util.HashMap

//Esta clase es para poder guardar las variables en la base de datos
class Usuario {
    var nombre: String? = null
    var edad: String? = null
    var email: String? = null
    var Reservas: HashMap<String?, HashMap<String?, String?>>? = null

    constructor() {

    }

    constructor(nombre: String?, edad: String?, email: String?) {
        this.nombre = nombre
        this.edad = edad
        this.email = email
    }
}