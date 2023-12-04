package com.technogenis.expensior.adapter
import android.annotation.SuppressLint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.model.TransferModel

class TransferAdapter(private val transferList: ArrayList<TransferModel>) :
 RecyclerView.Adapter<TransferAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transection_list_layout,parent,false)

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransferAdapter.ViewHolder, position: Int) {
       val transaction = transferList[position]

        holder.date.text = transaction.date.toString()
        holder.category.text = "transaction"
        holder.paymentMethod.text = "${transaction.fromBank} To ${transaction.toBank}"
        holder.amount.text = "${Collections().poundSymbol} ${transaction.amount}"
        holder.time.text = transaction.time
        holder.notes.text = "Notes: ${transaction.notes}"
    }


    override fun getItemCount(): Int {
        return transferList.size
    }

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){

        //custom layout fields
        val date : TextView = itemView.findViewById(R.id.tv_date)
        val category : TextView = itemView.findViewById(R.id.tv_category)
        val paymentMethod : TextView = itemView.findViewById(R.id.tv_paymentMethod)
        val amount : TextView = itemView.findViewById(R.id.tv_amount)
        val time : TextView = itemView.findViewById(R.id.tv_time)
        val notes : TextView = itemView.findViewById(R.id.tv_notes)

    }
}