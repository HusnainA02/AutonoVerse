package com.example.autonoverse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactoAdapter(private var contactosList: MutableList<ContactoBBDD>) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    class ContactoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var referencia: TextView = itemView.findViewById(R.id.Referencia)
        var titulo: TextView = itemView.findViewById(R.id.Titulo)
        var idUsuario: TextView = itemView.findViewById(R.id.IdUsuario)
        var correo: TextView = itemView.findViewById(R.id.Correo)
        var numero: TextView = itemView.findViewById(R.id.Numero)
        var horasDisponibles: TextView = itemView.findViewById(R.id.HorasDisponibles)
        var otros: TextView = itemView.findViewById(R.id.Otros)
        var urlArchivo: TextView = itemView.findViewById(R.id.UrlArchivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_solicitudes, parent, false)
        return ContactoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = contactosList[position]
        holder.referencia.text = contacto.referencia
        holder.titulo.text = contacto.titulo
        holder.idUsuario.text = contacto.id
        holder.correo.text = contacto.correo
        holder.numero.text = contacto.numero
        holder.horasDisponibles.text = contacto.horasDisponibles
        holder.otros.text = contacto.otros
        holder.urlArchivo.text = contacto.urlArchivo
    }

    override fun getItemCount(): Int = contactosList.size

    fun updateData(newList: List<ContactoBBDD>) {
        contactosList.clear()
        contactosList.addAll(newList)
        notifyDataSetChanged()
    }
}
