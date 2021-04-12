package com.example.restaurante

class Usuario {
    var nombre: String? = null
    var edad: String? = null
    var email: String? = null

    constructor() {

    }
    constructor(nombre: String?, edad: String?, email: String?) {
        this.nombre = nombre
        this.edad = edad
        this.email = email
    }
}