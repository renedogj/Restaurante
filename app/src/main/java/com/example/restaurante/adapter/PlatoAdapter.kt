package com.example.restaurante.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurante.R
import com.example.restaurante.model.PlatoModel

class PlatoAdapter (
    private val context: Context,
    private val list:List<PlatoModel>


): RecyclerView.Adapter<PlatoAdapter.platoViewHolder>(){

    class platoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var  imageView: ImageView?=null
        var txtNombre: TextView?=null
        var txtDescripcion: TextView?=null

        init{
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtNombre = itemView.findViewById(R.id.txtNombre) as TextView
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): platoViewHolder {
        return platoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_plato_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: platoViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].imagen)
            .into(holder.imageView!!)
        holder.txtNombre!!.text = StringBuilder().append(list[position].nombre)
        holder.txtDescripcion!!.text = StringBuilder().append(list[position].descripcion)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}