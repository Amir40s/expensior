package com.technogenis.expensior.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.MainActivity
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private  lateinit var monthlyIncome : String
    private  lateinit var monthlyExpense : String
    private  lateinit var jobTitle : String
    private  lateinit var jobLocation : String

      var loadingBar = LoadingBar(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()


        binding.btnSubmit.setOnClickListener{
            loadingBar.ShowDialog("Please wait")
            monthlyIncome = binding.edMonthlyIncome.text.toString()
            monthlyExpense = binding.edMonthlyExpense.text.toString()
            jobTitle = binding.edJobTitle.text.toString()
            jobLocation = binding.edJobLocation.text.toString()

            if (isValid(monthlyIncome,monthlyExpense,jobTitle,jobLocation))
            {
                saveProfileData()
            }
        }



    }

    private fun saveProfileData() {
        val userUID = auth.currentUser?.uid
        val map = hashMapOf<String,Any>(
            "monthlyIncome" to monthlyIncome,
            "monthlyExpense" to monthlyExpense,
            "jobTitle" to jobTitle,
            "jobLocation" to jobLocation,
            "jobLocation" to jobLocation,
            "isProfileCompleted" to "true",
        )

        firestore.collection("users").document(userUID.toString())
            .update(map)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    loadingBar.HideDialog()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }

    private fun isValid(monthlyIncome: String, monthlyExpense: String,
                        jobTitle: String, jobLocation: String): Boolean
    {
        if (monthlyIncome.isEmpty() || monthlyExpense.isEmpty()
            || jobTitle.isEmpty() || jobLocation.isEmpty())
        {
            Toast.makeText(this,"Filled missing fields",Toast.LENGTH_SHORT).show()
            loadingBar.HideDialog()
            return false
        }
        return true

    }
}