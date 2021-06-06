package com.example.restaurante.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.R

class IngredienteAdapter (
    private val context: Context,
    private val ingredienteList: List<String>,
    private val color: Int? = null
): RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder>(){

    class IngredienteViewHolder(itemView: View, color: Int?) : RecyclerView.ViewHolder(itemView) {
        var tvIngrediente: TextView? = null
        var cardIngrediente: CardView? = null

        init{
            tvIngrediente = itemView.findViewById(R.id.tvIngrediente) as TextView
            cardIngrediente = itemView.findViewById(R.id.cardIngrediente) as CardView

            if (color != null){
                cardIngrediente!!.setBackgroundColor(color)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredienteViewHolder {
        return IngredienteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_ingredientes_item, parent, false),
            color
        )
    }

    override fun onBindViewHolder(ingredienteViewHolder: IngredienteViewHolder, position: Int) {
        ingredienteViewHolder.tvIngrediente!!.text = ingredienteList[position]
    }

    override fun getItemCount(): Int {
        return ingredienteList.size
    }
}