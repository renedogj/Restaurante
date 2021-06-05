package com.example.restaurante.listener

import com.example.restaurante.model.MenuModel

interface IMenuLoadListener {
    fun onMenuLoadSuccess(menuModel: MenuModel?)
    fun onMenuLoadFailed(message:String?)
}