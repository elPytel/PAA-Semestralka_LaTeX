package com.example.semestralka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FormulasAdapter(
    private val formulas: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<FormulasAdapter.FormulaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormulaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return FormulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormulaViewHolder, position: Int) {
        holder.textView.text = formulas[position]
        holder.itemView.setOnClickListener {
            onItemClick(formulas[position])
        }
    }

    override fun getItemCount(): Int {
        return formulas.size
    }

    class FormulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
