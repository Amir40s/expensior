package com.technogenis.expensior.start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.MainActivity
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivitySignUpBinding
import java.util.Objects

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    // email validation pattern
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    // variables
    private lateinit var fullName : String
    private lateinit var phone : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var conPassword : String
    private lateinit var auth: FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    private var loadingBar = LoadingBar(this)


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser!=null)
        {
            Firebase.auth.signOut()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()


        binding.backImage.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        // button click listener
        binding.btnCreate.setOnClickListener{
            // custom loader
            loadingBar.ShowDialog("please wait")
            // get all input fields in variables
            fullName = binding.edName.text.toString()
            phone = binding.edPhoneNumber.text.toString()
            email = binding.edEmail.text.toString()
            password = binding.edPassword.text.toString()
            conPassword = binding.edConfirmPassword.text.toString()

            if (isValid(fullName,phone,email,password,conPassword))
            {
                createAccountWithEmailAuth(email,password)
            }



        }


    }

    // function to create account using Email & Password Authentication
    private fun createAccountWithEmailAuth(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val userUID = auth.currentUser?.uid.toString()
                    saveUserData(userUID)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,
                    ).show()
                    loadingBar.HideDialog()

                }
            }
    }

    // function to save data in cloud firestore
    private fun saveUserData(userUID: String?) {
        // use to store data in key for save data in firestore
        val userData = hashMapOf(
            "userUID" to userUID,
            "fullName" to fullName,
            "phone" to phone,
            "email" to email,
            "password" to password,
        )

        firestore.collection("users").document(userUID.toString())
            .set(userData)
            .addOnCompleteListener{
                val intent  =Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                loadingBar.HideDialog()
            }.addOnFailureListener{
                loadingBar.HideDialog()
            }

    }


    // function to check empty fields
    private fun isValid(fullName : String, phone : String,
                        email : String, password : String,conPassword : String) : Boolean{

       // condition apply to check empty fields
        if (fullName.isEmpty())
        {
            binding.edName.error = "Full Name Required"
            binding.edName.requestFocus()
            loadingBar.HideDialog()
            return false
        }
        if (phone.isEmpty())
        {
            binding.edPhoneNumber.error = "Phone Number Required"
            binding.edPhoneNumber.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (email.isEmpty())
        {
            binding.edEmail.error = "Email Required"
            binding.edEmail.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (!isValidEmail(email))
        {
            binding.edEmail.error = "enter valid email address"
            binding.edEmail.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (password.isEmpty())
        {
            binding.edPassword.error = "Password Required"
            binding.edPassword.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (conPassword.isEmpty())
        {
            binding.edConfirmPassword.error = "Confirm Password Required"
            binding.edConfirmPassword.requestFocus()
            loadingBar.HideDialog()
            return false
        }

        if (password != conPassword)
        {
            Toast.makeText(this,"Password Not Match",Toast.LENGTH_SHORT).show()
            loadingBar.HideDialog()
            return false
        }
        
        if (password.length < 8)
        {
            Toast.makeText(this,"Minimum 8 character required",Toast.LENGTH_SHORT).show()
            loadingBar.HideDialog()
            return false
        }
        
        return true
    }
    // function for email validation
    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }
}