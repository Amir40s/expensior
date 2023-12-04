package com.technogenis.expensior.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.technogenis.expensior.R
import com.technogenis.expensior.bottomsheet.PaymentMethodBottomSheetFragment
import com.technogenis.expensior.databinding.FragmentPaymentMethodBottomSheetBinding
import com.technogenis.expensior.model.PaymentCategoryModel
import com.technogenis.expensior.utils.DataTransferInterface

class PaymentCategoryAdapter(private val paymentCategoryList: ArrayList<PaymentCategoryModel>,
                             private val dataTransferInterface: DataTransferInterface

) : RecyclerView.Adapter<PaymentCategoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.payment_category_list_layout,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = paymentCategoryList[position]
        holder.tv_paymentCategory.text = data.name

        holder.itemView.setOnClickListener {
           dataTransferInterface.getData(data.name.toString())
        }

    }

    override fun getItemCount(): Int {
       return paymentCategoryList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tv_paymentCategory : TextView = itemView.findViewById(R.id.tv_paymentCategory)
    }


}