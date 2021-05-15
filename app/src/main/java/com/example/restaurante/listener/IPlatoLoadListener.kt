package com.example.restaurante.listener

import com.example.restaurante.model.PlatoModel

interface IPlatoLoadListener {
    fun onPlatoLoadSuccess(platoModelList:List<PlatoModel>?)
    fun onPlatoLoadFailed(message:String?)
}