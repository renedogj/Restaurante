package com.example.restaurante
//Esta clase es para poder guardar las variables en la base de datos
class Usuario (
    val nombreUsuario: String? = null,
    val apellidos:String?=null,
    val correo: String? = null,
    var usuarioId: String? = null,
    val contrasena: String? = null,
    val imagen:String?=null,
    val descripcion:String?=null
)