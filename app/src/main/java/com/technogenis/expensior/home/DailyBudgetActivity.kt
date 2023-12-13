package com.technogenis.expensior.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.technogenis.expensior.R
import com.technogenis.expensior.adapter.DailyBudgetAdapter
import com.technogenis.expensior.adapter.HistoryAdapter
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivityDailyBudgetBinding
import com.technogenis.expensior.model.DailyBudgetModel
import com.technogenis.expensior.model.HistoryModel

class DailyBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyBudgetBinding
    private lateinit var budget : String
    private lateinit var spend : String
    private var notes : String = ""
    private var remaining : String = ""
    private lateinit var userSpend : String
    private lateinit var userUID : String
    private var currentDateTime =  CurrentDateTime(this)
    private var loadingBar =  LoadingBar(this)
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var adapter : DailyBudgetAdapter
    private lateinit var budgetList: ArrayList<DailyBudgetModel>

    override fun onStart() {
        super.onStart()
        loadingBar.ShowDialog("please wait")
        getBudget(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.currentUser?.uid.toString()

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)
        getListDetails()

        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.topLayout.backText.text = "Daily Budget Calculator"

        binding.btnSet.setOnClickListener {
            loadingBar.ShowDialog("please wait")
            budget = binding.edBudget.text.toString()
            if (budget.isEmpty()){
                binding.edBudget.error = "amount required"
                binding.edBudget.requestFocus()
                loadingBar.HideDialog()
                return@setOnClickListener
            }else{
                saveBudget()
            }
        }

        binding.btnSpend.setOnClickListener {
            loadingBar.ShowDialog("please wait")
            userSpend = binding.edBudget.text.toString()
            notes = binding.edNote.text.toString()
            if (userSpend.isEmpty()){
                binding.edBudget.error = "amount required"
                binding.edBudget.requestFocus()
                loadingBar.HideDialog()
                return@setOnClickListener
            }else{
                getBudget(true)
            }
        }

        binding.btnClear.setOnClickListener {
            clearBudet()
        }



    }

    private fun clearBudet() {
        loadingBar.ShowDialog("please wait")

        firestore.collection("Budget").document(userUID).delete()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    getBudget(false)
                    getListDetails()
                    Toast.makeText(this,"Clear",Toast.LENGTH_SHORT).show()
                }
            }.addOnCompleteListener {
                deleteSubcollection()
            }
    }

    private fun deleteSubcollection() {
       firestore.collection("Budget").document(userUID)
           .collection("details")
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
                          firestore.collection("Budget").document(userUID)
                              .collection("details")
                              .document(snapShot.document.getString("id").toString())
                              .delete()
                       }
                   }

                   adapter.notifyDataSetChanged()
                   loadingBar.HideDialog()
                   getBudget(false)
                   getListDetails()
               }


           })
    }


    private fun saveBudget() {
        val map = hashMapOf<String,Any>(
            "budget" to budget
        )
       firestore.collection("Budget")
           .document(userUID)
           .set(map)
           .addOnCompleteListener {
               if (it.isSuccessful) {
                   getBudget(false)
                   Toast.makeText(this,"Budget Set completed...",Toast.LENGTH_SHORT).show()
               }
           }

    }

    @SuppressLint("SetTextI18n")
    private fun getBudget(click : Boolean) {
       firestore.collection("Budget")
           .document(userUID)
           .get()
           .addOnSuccessListener {document  ->
               if (document.exists()){

                   budget = if (document.contains("budget")){
                       document.getString("budget").toString()
                   }else{
                       "0"
                   }
                   if (document.contains("spend")) {
                       spend = document.getString("spend").toString()
                   } else{
                      spend = "0"
                   }

                    if (document.contains("remaining")) {
                        remaining =document.getString("remaining").toString()
                   }else{
                        remaining ="0"
                   }

                   if (click){
                       spend = (spend.toInt() + userSpend.toInt()).toString()
                       remaining = (budget.toInt() - spend.toInt()).toString()
                   }
                   binding.tvBudget.text = "Budget: $budget"
                   binding.tvSpend.text = "Spend: $spend"
                   binding.tvRemaining.text = "Remaining: $remaining"

                   if (click){
                       updateRecord(spend,remaining)
                   }else{
                       loadingBar.HideDialog()
                   }

               }else{
                   loadingBar.HideDialog()
                   binding.tvBudget.text = "not set"
                   binding.tvSpend.text = "not found"
                   binding.tvRemaining.text = "not found"
                   Toast.makeText(this,"no budget found",Toast.LENGTH_SHORT).show()
               }
           }

    }

    private fun updateRecord(spend : String,remaining : String) {
        val map = hashMapOf<String,Any>(
            "spend" to spend,
            "remaining" to remaining,
        )
        firestore.collection("Budget")
            .document(userUID)
            .update(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    saveRecord(spend)
                    getBudget(false)
                    Toast.makeText(this,"Spend updated...",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveRecord(spend : String) {
        val id  =  firestore.collection("Budget").document().id
        val map = hashMapOf<String,Any>(
            "spend" to spend,
            "note" to notes,
            "id" to id,
        )
        firestore.collection("Budget").document(userUID)
            .collection("details")
            .document(id)
            .set(map).addOnCompleteListener {
                if (it.isSuccessful){
                    getBudget(false)
                    getListDetails()
                    Toast.makeText(this,"spend save",Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun getListDetails() {
        budgetList = arrayListOf<DailyBudgetModel>()
        adapter = DailyBudgetAdapter(budgetList)
        binding.recycleView.adapter = adapter

        firestore.collection("Budget")
            .document(userUID)
            .collection("details")
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
                            budgetList.add(snapShot.document.toObject(DailyBudgetModel::class.java))
                        }
                    }

                    adapter.notifyDataSetChanged()
                }


            })
    }
}