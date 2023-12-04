package com.technogenis.expensior.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.technogenis.expensior.R
import com.technogenis.expensior.adapter.HistoryAdapter
import com.technogenis.expensior.adapter.TransferAdapter
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.databinding.ActivityTransectionBinding
import com.technogenis.expensior.model.HistoryModel
import com.technogenis.expensior.model.TransferModel

class TransectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransectionBinding
    private lateinit var adapter: TransferAdapter
    private lateinit var transferList: ArrayList<TransferModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    lateinit var userUID : String
    private  var collections = Collections()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        fireStore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topLayout.backText.text = "Transaction"
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)

        transferList = arrayListOf<TransferModel>()
        adapter = TransferAdapter(transferList)
        binding.recycleView.adapter = adapter
        getFirestoreData()
    }

    private fun getFirestoreData() {
        fireStore.collection(collections.userIncome)
            .document(userUID)
            .collection("transferDetails")
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
                            transferList.add(snapShot.document.toObject(TransferModel::class.java))
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