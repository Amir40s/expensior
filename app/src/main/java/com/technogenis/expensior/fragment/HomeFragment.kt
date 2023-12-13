package com.technogenis.expensior.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.technogenis.expensior.R
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.databinding.FragmentHomeBinding
import com.technogenis.expensior.home.DailyBudgetActivity
import com.technogenis.expensior.home.ExpenseHistoryActivity
import com.technogenis.expensior.home.IncomeActivity
import com.technogenis.expensior.home.TransectionActivity
import com.technogenis.expensior.home.TransferActivity
import java.util.Locale


class HomeFragment : Fragment() {

    // variables classes and objects
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUID : String
    private lateinit var income : String
    private lateinit var expense : String
    private lateinit var currentDateTime : CurrentDateTime
    private  var balance : Int = 0
    private var fabVisible = false

    override fun onStart() {
        super.onStart()
        getValues()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.uid.toString()

        currentDateTime = activity?.let { CurrentDateTime(it) }!!

        // functions
        imageSlider()
        floatingActionButton()

        // floating action Button visibility
        binding.fabExpense.visibility = View.GONE
        binding.fabIncome.visibility = View.GONE
        binding.fabTransfer.visibility = View.GONE
        binding.fabTransaction.visibility = View.GONE
        // floating action text visibility
        binding.expenseActionText.visibility = View.GONE
        binding.incomeActionText.visibility = View.GONE
        binding.transferActionText.visibility = View.GONE
        binding.transactionActionText.visibility = View.GONE

        //income button
        binding.addIncomeCard.setOnClickListener{
            moveToActivity("income")
        }
        //expense button
        binding.addExpenseCard.setOnClickListener{
            moveToActivity("expense")
        }

        // Transaction button to move to Transaction activity
        binding.transactionCard.setOnClickListener {
            val intent = Intent(activity,TransectionActivity::class.java)
            startActivity(intent)
        }

        // transfer payment button to move to Transfer activity
        binding.transferCard.setOnClickListener {
            val intent = Intent(activity,TransferActivity::class.java)
            startActivity(intent)
        }

        // Daily Budget button to move to Daily Budget activity
        binding.dailyBudgetCard.setOnClickListener {
            startActivity(Intent(activity,DailyBudgetActivity::class.java))
        }

        return binding.root
    }

    private fun moveToActivity(activityName : String){
        val intent = Intent(activity,IncomeActivity::class.java)
        intent.putExtra("activityName",activityName)
        startActivity(intent)
    }
    // function to get values from firebaseFirestore database
    private fun getValues() {
        firebaseFirestore.collection("userExpense")
            .document(userUID)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val snapshot = task.result

                    income = if (snapshot.contains("income${currentDateTime?.getCurrentMonth().toString().lowercase(Locale.ROOT)}")) {
                        snapshot.get("income${currentDateTime?.getCurrentMonth().toString().lowercase(Locale.ROOT)}").toString()
                    }else{
                        "0"
                    }

                    expense = if (snapshot.contains("expense${currentDateTime?.getCurrentMonth().toString().lowercase(Locale.ROOT)}")) {
                        snapshot.get("expense${currentDateTime?.getCurrentMonth().toString().lowercase(Locale.ROOT)}").toString()
                    }else{
                        "0"
                    }

                    balance = income.toInt() - expense.toInt()
                    binding.detailsInclude.tvIncome.text = income
                    binding.detailsInclude.tvExpense.text = expense
                    binding.detailsInclude.tvBalance.text = balance.toString()
                    binding.detailsInclude.tvMonth.text = currentDateTime.getCurrentMonth().toString()
                }
            }
    }



    private fun imageSlider() {
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://www.cflowapps.com/wp-content/uploads/2021/03/expense-management-process-flow.png","Manage Daily Expense"))
        imageList.add(SlideModel("https://www.wellybox.com/wp-content/uploads/2021/01/9-expense-manager-app.jpg","Add Expense Details"))
        imageList.add(SlideModel("https://img.freepik.com/free-photo/office-table-with-smartphone-it-view-from_93675-132333.jpg?size=626&ext=jpg&ga=GA1.1.1826414947.1699056000&semt=ais","Check History"))
        imageList.add(SlideModel("https://img.freepik.com/free-photo/asian-woman-working-through-paperwork_53876-138148.jpg","Easy Calculation"))

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun floatingActionButton() {

        fabVisible = false
        binding.fabAdd.setOnClickListener{
            if (!fabVisible) {

                // if its false we are displaying home fab
                // and settings fab by changing their
                // visibility to visible.
                binding.fabExpense.show()
                binding.fabIncome.show()
                binding.fabTransfer.show()
                binding.fabTransaction.show()

               // expense button with text
                binding.fabExpense.visibility = View.VISIBLE
                binding.expenseActionText.visibility = View.VISIBLE
                // income button with text
                binding.fabIncome.visibility = View.VISIBLE
                binding.incomeActionText.visibility = View.VISIBLE
                // transfer button with text
                binding.fabTransfer.visibility = View.VISIBLE
                binding.transferActionText.visibility = View.VISIBLE
                // transaction button with text
                binding.fabTransaction.visibility = View.VISIBLE
                binding.transactionActionText.visibility = View.VISIBLE

                // on below line we are checking image icon of add fab
                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_close))

                // on below line we are changing
                // fab visible to true
                fabVisible = true
            } else {

                // if the condition is true then we
                // are hiding home and settings fab
                binding.fabExpense.hide()
                binding.fabIncome.hide()
                binding.fabTransfer.hide()
                binding.fabTransaction.hide()

                // expense button with text
                binding.fabExpense.visibility = View.GONE
                binding.expenseActionText.visibility = View.GONE
                // income button with text
                binding.fabIncome.visibility = View.GONE
                binding.incomeActionText.visibility = View.GONE
                // transfer button with text
                binding.fabTransfer.visibility = View.GONE
                binding.transferActionText.visibility = View.GONE
                // transaction button with text
                binding.fabTransaction.visibility = View.GONE
                binding.transactionActionText.visibility = View.GONE


                // on below line we are changing image source for add fab
                binding.fabAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_add))

                // on below line we are changing
                // fab visible to false.
                fabVisible = false
            }
        }

        // on below line we are adding
        // click listener for our home fab
        binding.fabExpense.setOnClickListener {
            // on below line we are displaying a toast message.
           moveToActivity("expense")
        }

        // on below line we are adding on
        // click listener for settings fab
        binding.fabIncome.setOnClickListener {
            // on below line we are displaying a toast message
           moveToActivity("income")
        }

        binding.fabTransfer.setOnClickListener{
            val intent = Intent(activity, TransferActivity::class.java)
            startActivity(intent)
        }
    }

        }


