package com.technogenis.expensior.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.MainActivity
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivityLoginBinding
import com.technogenis.expensior.home.ProfileActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    private lateinit var email : String; lateinit var password : String
    private lateinit var auth: FirebaseAuth

    val loadingBar = LoadingBar(this)

    private lateinit var firestore : FirebaseFirestore


    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user!=null)
        {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // connection between database and mobile app
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.btnSignIn.setOnClickListener{
            loadingBar.ShowDialog("please wait")
            email = binding.edEmail.text.toString()
            password = binding.edPassword.text.toString()

           if (isValid(email,password))
           {
               //function to Sign in account
            signWithEmailAuth(email,password)
           }
        }

        // button to move Sign up screen
        binding.createAccountText.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }


    }

    private fun signWithEmailAuth(email: String, password: String)
    {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   checkProfile()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }

    }

    private fun isValid(email : String, password : String) : Boolean{

         if (email.isEmpty())
         {
             Toast.makeText(this,"Password Required",Toast.LENGTH_SHORT).show()
             return false
         }

         if (password.isEmpty())
         {
             Toast.makeText(this,"Password Required",Toast.LENGTH_SHORT).show()
             return false
         }

          if (!isValidEmail(email))
          {
              binding.edEmail.error = "enter valid email address"
              binding.edEmail.requestFocus()
              loadingBar.HideDialog()
              return false
          }



         return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

    private fun checkProfile() {
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
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        println(document.getString("email"))
                        Log.d("TAG", "checkPrifile: $data")
                    }
                }
        }else{
            Toast.makeText(this,"Account Not Found",Toast.LENGTH_SHORT).show()
        }

    }
}