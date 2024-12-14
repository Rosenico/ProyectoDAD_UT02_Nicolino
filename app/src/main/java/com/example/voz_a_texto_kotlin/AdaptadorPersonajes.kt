package com.example.voz_a_texto_kotlin

import Personaje
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorPersonajes(
    private var listaPersonajes: ArrayList<Personaje>,
    val onItemClick: (Personaje) -> Unit
) : RecyclerView.Adapter<AdaptadorPersonajes.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_personaje, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personaje = listaPersonajes[position]
        holder.ivImagen.setImageResource(personaje.imagenId)
        holder.tvNombre.text = personaje.nombre
        holder.tvInformacion.text = personaje.informacion

        holder.itemView.setOnClickListener {
            onItemClick(personaje)
        }
    }

    override fun getItemCount(): Int = listaPersonajes.size

    fun filtrar(listaFiltrada: ArrayList<Personaje>) {
        this.listaPersonajes = listaFiltrada
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagen: ImageView = itemView.findViewById(R.id.ivImagen)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvInformacion: TextView = itemView.findViewById(R.id.tvInformacion)
    }
}
