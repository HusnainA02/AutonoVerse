package com.example.autonoverse


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AnunciosAdapter(private var anunciosList: MutableList<AnuncioBBDD>) : RecyclerView.Adapter<AnunciosAdapter.AnuncioViewHolder>() {

    class AnuncioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView = itemView.findViewById(R.id.TitTrab)
        var referencia: TextView = itemView.findViewById(R.id.RefUsuTrab)
        var ciudad: TextView = itemView.findViewById(R.id.LocUsuTrab)
        var precio: TextView = itemView.findViewById(R.id.PrecioTrab)
        var disponibilidad: TextView = itemView.findViewById(R.id.DisponTrab)
        var descripcion: TextView = itemView.findViewById(R.id.DescripTrab)
        var experiencia: TextView = itemView.findViewById(R.id.ExperiTrab)
        var idUsuario: TextView = itemView.findViewById(R.id.IdUsuTrab)
        var fecha: TextView = itemView.findViewById(R.id.FechaTrab)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnuncioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_anuncios, parent, false)
        return AnuncioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnuncioViewHolder, position: Int) {
        val anuncio = anunciosList[position]
        holder.titulo.text = anuncio.titulo
        holder.referencia.text = "Ref.: ${anuncio.referencia}"
        holder.ciudad.text = "Ciudad: ${anuncio.ciudadAnuncio}"
        holder.precio.text = "A partir de: ${anuncio.precioAnuncio}€ / h"
        holder.disponibilidad.text = "Necesita: ${anuncio.disponibilidadAnuncio} h disponibilidad"
        holder.descripcion.text = "Descripción: ${anuncio.descripcion}"
        holder.experiencia.text = "Experiencia que necesita: ${anuncio.experiencia}"
        holder.idUsuario.text = "ID de usuario: ${anuncio.idUsuario}"
        holder.fecha.text = anuncio.getFecha()

    }

    override fun getItemCount(): Int = anunciosList.size

    fun updateData(newList: List<AnuncioBBDD>) {
        anunciosList.clear()
        anunciosList.addAll(newList)
        notifyDataSetChanged()
    }
}

