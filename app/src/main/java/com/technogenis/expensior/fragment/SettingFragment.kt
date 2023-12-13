package com.technogenis.expensior.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.FragmentSettingBinding
import com.technogenis.expensior.start.LoginActivity


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    var loadingBar = LoadingBar(activity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentSettingBinding.inflate(layoutInflater, container, false)

        binding.deleteCard.setOnClickListener {
            loadingBar.ShowDialog("deleting...")
            deleteAccount()
        }

        return binding.root
    }

    private fun deleteAccount() {
        Log.d("TAG", "deleteAccount")
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadingBar.HideDialog()
                Toast.makeText(activity,"Account Deleted..", Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }.addOnFailureListener { e ->
            loadingBar.HideDialog()
            Log.e(
                "TAG",
                "Something went wrong...",
                e
            )
        }
    }


}