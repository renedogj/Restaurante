package com.example.restaurante.model

class Reserva {
    var horaLlegada: Int? = null
    var minutoLlegada: Int? = null
    var nombre: String? = null
    var numPersonas: Int? = null
    var ususarip: String? = null

    constructor(horaLlegada: Int?, minutoLlegada: Int?, nombre: String?, numPersonas: Int?) {
        this.horaLlegada = horaLlegada
        this.minutoLlegada = minutoLlegada
        this.nombre = nombre
        this.numPersonas = numPersonas
    }

    constructor(horaLlegada: Int?, minutoLlegada: Int?,nombre: String?,numPersonas: Int?,ususarip: String?) {
        this.horaLlegada = horaLlegada
        this.minutoLlegada = minutoLlegada
        this.nombre = nombre
        this.numPersonas = numPersonas
        this.ususarip = ususarip
    }


}