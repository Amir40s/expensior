package com.technogenis.expensior.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.model.DailyBudgetModel
import com.technogenis.expensior.model.HistoryModel

// adapter to move data
 class DailyBudgetAdapter(private val budgetList: ArrayList<DailyBudgetModel>)
     : RecyclerView.Adapter<DailyBudgetAdapter.ViewHolder>() {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

         val itemView = LayoutInflater.from(parent.context).inflate(R.layout.budget_list_layout,parent,false)

         return ViewHolder(itemView)
     }

     @SuppressLint("SetTextI18n")
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val budget = budgetList[position]
         holder.amount.text = budget.spend.toString()
         holder.notes.text = budget.note.toString()


     }

     override fun getItemCount(): Int {
         return budgetList.size
     }

     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         //custom layout fields
         val amount : TextView = itemView.findViewById(R.id.tv_amount)
         val notes : TextView = itemView.findViewById(R.id.tv_notes)


     }
 }