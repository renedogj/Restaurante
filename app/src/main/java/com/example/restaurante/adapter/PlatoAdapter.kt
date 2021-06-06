package com.example.restaurante.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurante.InfoPlatosMenu
import com.example.restaurante.R
import com.example.restaurante.Reservas
import com.example.restaurante.model.PlatoModel

class PlatoAdapter (
    private val context: Context,
    private val platoList: List<PlatoModel>,
): RecyclerView.Adapter<PlatoAdapter.PlatoViewHolder>(){

    class PlatoViewHolder(context: Context,itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardViewPlato: CardView? = null
        var imageView: ImageView? = null
        var txtNombre: TextView? = null
        var txtDescripcion: TextView? = null
        var keyPlato: String? = null

        init{
            cardViewPlato = itemView.findViewById(R.id.cardViewPlato) as CardView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtNombre = itemView.findViewById(R.id.txtNombre) as TextView
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion) as TextView

            cardViewPlato!!.setOnClickListener {
                /*val intent: Intent?
                intent = Intent(context, InfoPlatosMenu::class.java)
                intent.putExtra("keyPlato", keyPlato)
                startActivity(context,intent,null)*/

                startActivity(context,Intent(context, Reservas::class.java),null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        return PlatoViewHolder(
            context,
            LayoutInflater.from(context).inflate(R.layout.layout_plato_item, parent, false)
        )
    }

    override fun onBindViewHolder(platoViewHolder: PlatoViewHolder, position: Int) {
        Glide.with(context)
            .load(platoList[position].imagen)
            .into(platoViewHolder.imageView!!)
        platoViewHolder.txtNombre!!.text = StringBuilder().append(platoList[position].nombre)
        platoViewHolder.txtDescripcion!!.text = StringBuilder().append(platoList[position].descripcion)
        platoViewHolder.keyPlato = platoList[position].key
    }

    override fun getItemCount(): Int {
        return platoList.size
    }
}