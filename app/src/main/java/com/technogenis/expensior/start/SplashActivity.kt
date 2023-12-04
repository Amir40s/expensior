package com.technogenis.expensior.start

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.MainActivity
import com.technogenis.expensior.R
import com.technogenis.expensior.databinding.ActivitySplashBinding
import com.technogenis.expensior.home.ProfileActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        checkPrifile()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        binding.animationView.visibility = View.VISIBLE
        binding.animationView.playAnimation()

//        val handler = Handler(Looper.myLooper()!!)
//        handler.postDelayed({
//
//        },3000)
    }

    private fun checkPrifile() {
        println("Enter")
        Log.d("TAG", "checkPrifile: Enter")
        val userUID = auth.currentUser?.uid
        if (userUID!=null)
        {
            firestore.collection("users").document(userUID)
                .get().addOnCompleteListener{ snapshot ->
                    if (snapshot.isSuccessful) {
                        val document = snapshot.result
                        val data = document.contains("isProfileCompleted")
                        if (data)
                        {
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else {
                            val intent = Intent(this,ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        println(document.getString("email"))
                        Log.d("TAG", "checkPrifile: $data")
                    }else{
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }else{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}