package com.technogenis.expensior.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.technogenis.expensior.adapter.HistoryAdapter
import com.technogenis.expensior.databinding.ActivityExpenseHistoryBinding
import com.technogenis.expensior.model.HistoryModel
import com.technogenis.expensior.model.PaymentCategoryModel
import com.technogenis.expensior.constant.Collections

class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityExpenseHistoryBinding
    lateinit var adapter : HistoryAdapter
    private lateinit var historyList: ArrayList<HistoryModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    lateinit var userUID : String
    lateinit var subCollection : String
    private  var collections = Collections()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        subCollection = intent.getStringExtra("type").toString()

        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topLayout.backText.text = "History"

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)

        historyList = arrayListOf<HistoryModel>()
        adapter = HistoryAdapter(historyList)
        binding.recycleView.adapter = adapter
        getFirestoreData(subCollection)


    }

    private fun getFirestoreData(subCollection : String) {
        firestore.collection(collections.userIncome)
            .document(userUID)
            .collection(subCollection)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?)
                {
                    if (error!=null)
                    {
                        Log.e("Firestore Error", "onEvent: ${error.message.toString()}" )
                        return
                    }
                    for (snapShot : DocumentChange in value?.documentChanges!!)
                    {
                        if (snapShot.type == DocumentChange.Type.ADDED)
                        {
                            historyList.add(snapShot.document.toObject(HistoryModel::class.java))
                        }
                    }

//                    if (historyList.size <=0)
//                    {
//                        binding.llNoResult.visibility = View.VISIBLE
//                    }else{
//                        binding.llNoResult.visibility = View.GONE
//                    }
                    adapter.notifyDataSetChanged()
                }


            })
    }
}