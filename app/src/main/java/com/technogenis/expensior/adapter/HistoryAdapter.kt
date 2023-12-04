package com.technogenis.expensior.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.model.HistoryModel

// adapter to move data
 class HistoryAdapter(private val paymentCategoryList: ArrayList<HistoryModel>)
     : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

         val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_list_layout,parent,false)

         return ViewHolder(itemView)
     }

     @SuppressLint("SetTextI18n")
     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val history = paymentCategoryList[position]
         holder.date.text = history.date.toString()
         holder.category.text = history.category.toString()
         holder.paymentMethod.text = history.paymentMethod
         holder.amount.text = "${Collections().poundSymbol} ${history.amount}"
         holder.time.text = history.time
         holder.notes.text = "Notes: ${history.notes}"

     }

     override fun getItemCount(): Int {
         return paymentCategoryList.size
     }

     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         //custom layout fields
         val date : TextView = itemView.findViewById(R.id.tv_date)
         val category : TextView = itemView.findViewById(R.id.tv_category)
         val paymentMethod : TextView = itemView.findViewById(R.id.tv_paymentMethod)
         val amount : TextView = itemView.findViewById(R.id.tv_amount)
         val time : TextView = itemView.findViewById(R.id.tv_time)
         val notes : TextView = itemView.findViewById(R.id.tv_notes)

     }
 }