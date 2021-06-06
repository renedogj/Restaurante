package com.example.restaurante.model

class Reserva {
    var key: String? = null
    var dia: Int? = null
    var mes: Int? = null
    var anno: Int? = null
    var horaLlegada: Int? = null
    var minutoLlegada: Int? = null
    var nombre: String? = null
    var numPersonas: Long? = null
    var ususario: String? = null

    constructor(){}

    constructor(horaLlegada: Int?, minutoLlegada: Int?, nombre: String?, numPersonas: Long?) {
        this.horaLlegada = horaLlegada
        this.minutoLlegada = minutoLlegada
        this.nombre = nombre
        this.numPersonas = numPersonas
    }

    constructor(
        horaLlegada: Int?,
        minutoLlegada: Int?,
        nombre: String?,
        numPersonas: Long?,
        ususario: String?
    ) {
        this.horaLlegada = horaLlegada
        this.minutoLlegada = minutoLlegada
        this.nombre = nombre
        this.numPersonas = numPersonas
        this.ususario = ususario
    }

    constructor(
        dia: Int?,
        mes: Int?,
        anno: Int?,
        horaLlegada: Int?,
        minutoLlegada: Int?,
        nombre: String?,
        numPersonas: Long?,
        ususario: String?
    ) {
        this.dia = dia
        this.mes = mes
        this.anno = anno
        this.horaLlegada = horaLlegada
        this.minutoLlegada = minutoLlegada
        this.nombre = nombre
        this.numPersonas = numPersonas
        this.ususario = ususario
    }
}