package com.example.restaurante.listener

import com.example.restaurante.model.Reserva

interface ILoadReservas {
    fun onReservaLoadSuccess(ReservasList:MutableList<Reserva>?)
    fun onReservaLoadFailed(message:String?)
}