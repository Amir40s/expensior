package com.technogenis.expensior.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.technogenis.expensior.databinding.FragmentCategoryBottomSheetBinding
import com.technogenis.expensior.databinding.FragmentPaymentMethodBottomSheetBinding
import com.technogenis.expensior.model.PaymentCategoryModel
import com.technogenis.expensior.utils.DataTransferInterface


class CategoryBottomSheetFragment : BottomSheetDialogFragment(), DataTransferInterface {

    //variables classes and objects
    private lateinit var binding: FragmentCategoryBottomSheetBinding
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
    ): View{
        // Inflate the layout for this fragment
        binding =  FragmentCategoryBottomSheetBinding.inflate(inflater, container, false)
        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        // set layout in recycleView
        binding.paymentRecycleView.layoutManager = LinearLayoutManager(activity)
        binding.paymentRecycleView.setHasFixedSize(true)

        categoryList = arrayListOf<PaymentCategoryModel>()
        adapter = PaymentCategoryAdapter(categoryList,this)
        binding.paymentRecycleView.adapter = adapter

        // function to get previous data from database
        getFirestoreData()

        // cancel button to dismiss the dialog
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // add new button to add category or bank name
        binding.btnAddNew.setOnClickListener{
            taskModel.dialogStatus.value = "category"
            dismiss()
        }

        return binding.root

    }

    private fun getFirestoreData() {
        // firebase query to get previous data
        firestore.collection("users")
            .document(userUID)
            .collection("category")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?)
                {
                    if (error!=null) {
                        Log.e("Firestore Error", "onEvent: ${error.message.toString()}" )
                        return
                    }
                    // for loop to get all document data in list
                    for (snapShot : DocumentChange in value?.documentChanges!!) {
                        if (snapShot.type == DocumentChange.Type.ADDED) {
                            categoryList.add(snapShot.document.toObject(PaymentCategoryModel::class.java))
                        }
                    }

                    // check data size in database
                    if (categoryList.size <=0) {
                        binding.llNoResult.visibility = View.VISIBLE
                    }else{
                        binding.llNoResult.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }


            })
    }

    // this function in connected to interface to observe data runtime
    override fun getData(categoryName: String) {
        taskModel.category.value = categoryName
        dismiss()
    }


}