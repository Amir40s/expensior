package com.technogenis.expensior.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.technogenis.expensior.R
import com.technogenis.expensior.adapter.PaymentCategoryAdapter
import com.technogenis.expensior.databinding.FragmentPaymentMethodBottomSheetBinding
import com.technogenis.expensior.model.PaymentCategoryModel
import com.technogenis.expensior.utils.DataTransferInterface
import io.grpc.internal.DnsNameResolver.SrvRecord


class PaymentMethodBottomSheetFragment(private  var status :String) : BottomSheetDialogFragment(), DataTransferInterface {


    private lateinit var binding: FragmentPaymentMethodBottomSheetBinding
    private lateinit var taskModel: ViewTaskModel
     lateinit var adapter: PaymentCategoryAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var userUID : String
    private lateinit var categoryList: ArrayList<PaymentCategoryModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        taskModel = ViewModelProvider(activity)[ViewTaskModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentPaymentMethodBottomSheetBinding.inflate(inflater, container, false)
        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        binding.paymentRecycleView.layoutManager = LinearLayoutManager(activity)
        binding.paymentRecycleView.setHasFixedSize(true)

        categoryList = arrayListOf<PaymentCategoryModel>()
        adapter = PaymentCategoryAdapter(categoryList,this)
        binding.paymentRecycleView.adapter = adapter

        getData()

        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.btnAddNew.setOnClickListener{
            setActions()
        }
        return binding.root
    }


    @SuppressLint("SuspiciousIndentation")
    private fun setActions() {
      taskModel.dialogStatus.value = "payment"
        dismiss()
    }

    private fun getData() {
        firestore.collection("users")
            .document(userUID)
            .collection("banks")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?)
                {
                   if (error!=null) {
                       Log.e("Firestore Error", "onEvent: ${error.message.toString()}" )
                       return
                   }
                    for (snapShot : DocumentChange in value?.documentChanges!!) {
                        if (snapShot.type == DocumentChange.Type.ADDED) {
                            categoryList.add(snapShot.document.toObject(PaymentCategoryModel::class.java))
                        }
                    }

                    if (categoryList.size <=0) {
                        binding.llNoResult.visibility = View.VISIBLE
                    }else{
                        binding.llNoResult.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }
    override fun getData(categoryName: String) {
        taskModel.name.value = categoryName
        if (status == "from"){
            taskModel.fromName.value = categoryName
        }
        if (status == "to"){
            taskModel.toName.value = categoryName
        }
        taskModel.bankCheck.value = status
        dismiss()
    }
}