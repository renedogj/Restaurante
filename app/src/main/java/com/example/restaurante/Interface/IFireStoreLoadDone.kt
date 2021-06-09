package com.example.restaurante.Interface

import com.example.restaurante.model.Movie

interface IFireStoreLoadDone {
    fun onFirebaseLoadSuccess(movieList:List<Movie>)
    fun onFirebaseLoadFailed(message:String)

}