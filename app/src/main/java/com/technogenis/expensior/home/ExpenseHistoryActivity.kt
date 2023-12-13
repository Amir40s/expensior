package com.technogenis.expensior.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.technogenis.expensior.R
import com.technogenis.expensior.adapter.HistoryAdapter
import com.technogenis.expensior.databinding.ActivityExpenseHistoryBinding
import com.technogenis.expensior.model.HistoryModel
import com.technogenis.expensior.model.PaymentCategoryModel
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.model.MonthSpinnerModel

class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityExpenseHistoryBinding
    lateinit var adapter : HistoryAdapter
    private lateinit var historyList: ArrayList<HistoryModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var currentDateTime = CurrentDateTime(this)

    lateinit var userUID : String
    lateinit var subCollection : String
    private  var collections = Collections()

    // spinner


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID  = firebaseAuth.currentUser?.uid.toString()

        subCollection = intent.getStringExtra("type").toString()

        monthDropDown()

        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topLayout.backText.text = "History"

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)


        getFirestoreData(subCollection, currentDateTime.getCurrentMonth().toString())


    }

    private fun monthDropDown() {
        val months = resources.getStringArray(R.array.Month)
        val spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,months)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getFirestoreData(subCollection,months[position].toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getFirestoreData(subCollection : String,month:String) {
        historyList = arrayListOf<HistoryModel>()
        adapter = HistoryAdapter(historyList)
        binding.recycleView.adapter = adapter
        firestore.collection(collections.userIncome)
            .document(userUID)
            .collection(subCollection)
            .whereEqualTo("month",month)
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

                    adapter.notifyDataSetChanged()
                }


            })
    }


}