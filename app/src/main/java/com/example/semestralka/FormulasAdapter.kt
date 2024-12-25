package com.example.semestralka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FormulasAdapter(
    private val formulas: List<EquationData>,
    private val onItemClick: (EquationData) -> Unit
) : RecyclerView.Adapter<FormulasAdapter.FormulaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormulaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return FormulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormulaViewHolder, position: Int) {
        holder.labelView.text = formulas[position].label
        holder.itemView.setOnClickListener {
            onItemClick(formulas[position])
        }
    }

    override fun getItemCount(): Int {
        return formulas.size
    }

    class FormulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
