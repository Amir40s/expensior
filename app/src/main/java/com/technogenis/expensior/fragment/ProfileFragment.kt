package com.technogenis.expensior.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userUID : String
//    private  var loadingBar = LoadingBar(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID = auth.currentUser?.uid.toString()

//        loadingBar.ShowDialog("please wait")
        getProfileData()

        return binding.root
    }

    // function to get profile data
    private fun getProfileData() {
       firestore.collection("users").document(userUID)
           .get().addOnCompleteListener{ task ->
               if (task.isSuccessful)
               {
//                   loadingBar.HideDialog()
                   val document = task.result
                   binding.tvName.text = document.getString("fullName")
                   binding.tvEmail.text = document.getString("email")
                   binding.tvPhone.text = document.getString("phone")
                   binding.tvMonthlyIncome.text = document.getString("monthlyIncome")
                   binding.tvMonthlyExpense.text = document.getString("monthlyExpense")
                   binding.tvJobTitle.text = document.getString("jobTitle")
                   binding.tvJobLocation.text = document.getString("jobLocation")
               }else{
//                   loadingBar.HideDialog()
               }
           }
    }


}