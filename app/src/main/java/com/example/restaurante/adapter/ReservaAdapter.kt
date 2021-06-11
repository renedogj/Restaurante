package com.example.restaurante.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.MenuDelDia
import com.example.restaurante.R
import com.example.restaurante.Reservas
import com.example.restaurante.eventbus.UpdateCartEvent
import com.example.restaurante.model.Reserva
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.activity_reservas.*
import org.greenrobot.eventbus.EventBus


class ReservaAdapter(
    private var context: Context,
    private var reservasList: MutableList<Reserva>
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var context: Context? = null
        var cardReserva: CardView? = null
        var reserva: Reserva? = null
        var btnBorrarReserva: ImageView? = null
        var tvDiaReserva: TextView? = null
        var tvHoraReserva: TextView? = null
        var tvNombreReserva: TextView? = null
        var tvNumeroPersonasReserva: TextView? = null

        init {
            cardReserva = itemView.findViewById(R.id.cardReserva) as CardView
            btnBorrarReserva = itemView.findViewById(R.id.btnBorrarReserva) as ImageView
            tvDiaReserva = itemView.findViewById(R.id.tvDiaReserva) as TextView
            tvHoraReserva = itemView.findViewById(R.id.tvHoraReserva) as TextView
            tvNombreReserva = itemView.findViewById(R.id.tvNombreReserva) as TextView
            tvNumeroPersonasReserva = itemView.findViewById(R.id.tvNumeroPersonasReserva) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        return ReservaViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_reserva_item, parent, false),
        )
    }

    override fun onBindViewHolder(reservaViewHolder: ReservaViewHolder, position: Int) {
        reservaViewHolder.context = context
        reservaViewHolder.reserva = reservasList[position]
        val dia: String?
        val mes: String?
        val minuto: String?
        if(reservasList[position].dia!! < 10){
            dia = "0" + reservasList[position].dia
        }else{
            dia = "${reservasList[position].dia}"
        }
        if(reservasList[position].mes!! < 10){
            mes = "0" + reservasList[position].mes
        }else{
            mes = "${reservasList[position].mes}"
        }
        if(reservasList[position].minutoLlegada!! < 10){
            minuto = "0" + reservasList[position].minutoLlegada
        }else{
            minuto = "${reservasList[position].minutoLlegada}"
        }
        reservaViewHolder.tvDiaReserva!!.text = "$dia/$mes/${reservasList[position].anno}"
        reservaViewHolder.tvHoraReserva!!.text = "${reservasList[position].horaLlegada}:$minuto"
        reservaViewHolder.tvNombreReserva!!.text = reservasList[position].nombre
        reservaViewHolder.tvNumeroPersonasReserva!!.text = "Reserva para ${reservasList[position].numPersonas} personas"

        reservaViewHolder.btnBorrarReserva!!.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("Borrar Reserva")
                .setMessage("¿Realmente quieres borrar la reserva seleccionada?")
                .setNegativeButton("CANCELAR") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("BORRAR") { dialog, _ ->
                    reservaViewHolder.cardReserva!!.visibility = View.GONE
                    var codigoTiempo = "${reservaViewHolder.reserva!!.anno}"
                    codigoTiempo += if (reservaViewHolder.reserva!!.mes!! < 10) {
                        "0${reservaViewHolder.reserva!!.mes}"
                    } else {
                        "${reservaViewHolder.reserva!!.mes}"
                    }
                    codigoTiempo += if (reservaViewHolder.reserva!!.dia!! < 10) {
                        "0${reservaViewHolder.reserva!!.dia}"
                    } else {
                        "${reservaViewHolder.reserva!!.dia}"
                    }
                    if (reservaViewHolder.reserva!!.horaLlegada!! >= 12 && reservaViewHolder.reserva!!.horaLlegada!! < 18) {
                        codigoTiempo += "01"
                    } else if (reservaViewHolder.reserva!!.horaLlegada!! >= 18 && reservaViewHolder.reserva!!.horaLlegada!! < 23) {
                        codigoTiempo += "02"
                    }
                    FirebaseDatabase.getInstance().getReference("Reservas").child(codigoTiempo).child("numTotalReservas").get()
                        .addOnSuccessListener {
                            val numPersonasBase = it.value as Long?
                            val total = numPersonasBase!! - reservaViewHolder.reserva!!.numPersonas!!
                            val long: Long = 0
                            if (total == long) {
                                FirebaseDatabase.getInstance().getReference("Reservas")
                                    .child(codigoTiempo).child("numTotalReservas").removeValue()
                            } else {
                                FirebaseDatabase.getInstance().getReference("Reservas")
                                    .child(codigoTiempo).child("numTotalReservas")
                                    .setValue(total)
                            }
                        }.addOnFailureListener {

                        }
                    val user = Firebase.auth.currentUser
                    FirebaseDatabase.getInstance().getReference("Reservas").child(codigoTiempo).child(reservaViewHolder.reserva!!.key!!).removeValue()
                    FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
                        .child("Reservas").child(codigoTiempo)
                        .child(reservaViewHolder.reserva!!.key!!).removeValue()
                }
                .create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return reservasList.size
    }
}