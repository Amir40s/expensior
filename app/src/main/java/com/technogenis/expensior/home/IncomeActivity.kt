package com.technogenis.expensior.home

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.R
import com.technogenis.expensior.bottomsheet.CategoryBottomSheetFragment
import com.technogenis.expensior.bottomsheet.PaymentMethodBottomSheetFragment
import com.technogenis.expensior.bottomsheet.ViewTaskModel
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivityIncomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityIncomeBinding
    private var currentDateTime =  CurrentDateTime(this)
    private lateinit  var income : String
    private lateinit  var expense : String
    private var incomeData : String = "0"
    private var expenseData : String = "0"
    private lateinit  var category : String
    private lateinit  var paymentMethod : String
    private var notes : String = ""
    private lateinit var date : String
    private lateinit var time : String
    private lateinit var userUID : String
    private lateinit var activityName : String

    private lateinit var bottomSheetTaskModel: ViewTaskModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var loadingBar = LoadingBar(this)

    private  var collections = Collections()

    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("hh:mm a",Locale.US)

    override fun onStart() {
        super.onStart()
        setCurrentDateTime()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetTaskModel = ViewModelProvider(this)[ViewTaskModel::class.java]

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.currentUser?.uid.toString()

        observeData()

         activityName = intent.getStringExtra("activityName").toString()
        if (activityName == "income")
        {
            binding.topLayout.backText.text = "Income"
            binding.includeDateField.tvLabel.text = "Income"
        }
        if (activityName == "expense"){

            binding.topLayout.backText.text = "Expense"
            binding.includeDateField.tvLabel.text = "Expense"
        }

        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.includeDateField.tvDate.setOnClickListener{
            showDateDialog(binding.includeDateField.tvDate)
        }

        binding.includeDateField.tvTime.setOnClickListener {
            TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.includeDateField.tvCategory.setOnClickListener {
            CategoryBottomSheetFragment().show(supportFragmentManager,"categorySheetTag")
        }

        binding.includeBottom.btnCategory.setOnClickListener {
            CategoryBottomSheetFragment().show(supportFragmentManager,"categorySheetTag")
        }

        binding.includeDateField.tvPaymentMethod.setOnClickListener {
            PaymentMethodBottomSheetFragment("").show(supportFragmentManager,"paymentSheetTag")
        }
        binding.includeBottom.btnPaymentMethod.setOnClickListener {
            PaymentMethodBottomSheetFragment("").show(supportFragmentManager,"paymentSheetTag")
        }

        binding.includeBottom.btnSave.setOnClickListener {

            loadingBar.ShowDialog("Please wait")
            income = binding.includeDateField.edIncome.text.toString()
            expense = binding.includeDateField.edIncome.text.toString()
            category = binding.includeDateField.tvCategory.text.toString()
            paymentMethod = binding.includeDateField.tvPaymentMethod.text.toString()
            notes = binding.includeDateField.tvNotes.text.toString()

            if (isValid(income,category,paymentMethod)) {
                getIncomeDetails()
            }
        }
    }

    private fun getIncomeDetails() {

        var incomeStatus: Boolean
        var expenseStatus: Boolean
        println(userUID)

        firestore.collection(collections.userIncome)
            .document(userUID)
            .get()
            .addOnCompleteListener{ snapShot ->
                if (snapShot.isSuccessful)
                {
                    val document = snapShot.result
                    if (document.contains("income")) {
                        incomeData =  document.get("income").toString()
                        income = (incomeData.toInt() + income.toInt()).toString()
                        incomeStatus = true
                    }else{
                        incomeData = "0"
                        incomeStatus = false
                        income = (incomeData.toInt() + income.toInt()).toString()
                    }
                    if (document.contains("expense")) {
                        expenseData =  document.get("expense").toString()
                        expense = (expenseData.toInt() + expense.toInt()).toString()
                        expenseStatus = true
                    }else{
                        expenseData = "0"
                        expenseStatus = false
                        expense = (expenseData.toInt() + expense.toInt()).toString()
                    }
                    saveData(income,expense,incomeStatus,expenseStatus,activityName)
                }
            }.addOnFailureListener{
                Toast.makeText(this,"something went wrong...",Toast.LENGTH_SHORT).show()
                loadingBar.HideDialog()
            }
    }

    private fun saveData(income : String,expense : String,incomeStatus : Boolean,expenseStatus : Boolean,activityName : String) {

       if (activityName == "income") {
           val map = hashMapOf<String,Any>(
               "income" to income,
           )
           if (incomeStatus) {
               firestore.collection(collections.userIncome)
                   .document(userUID)
                   .update(map)
                   .addOnCompleteListener {
                       if (it.isSuccessful) {
                           saveIncomeDetails(activityName)
                       }
                   }
           }else{
               if (expenseStatus){
                   firestore.collection(collections.userIncome)
                       .document(userUID)
                       .update(map)
                       .addOnCompleteListener {
                           if (it.isSuccessful) {
                               saveIncomeDetails(activityName)
                           }
                       }
               }else{
                   firestore.collection(collections.userIncome)
                       .document(userUID)
                       .set(map)
                       .addOnCompleteListener {
                           if (it.isSuccessful) {
                               saveIncomeDetails(activityName)
                           }
                       }
               }
           }
       }
        if (activityName == "expense"){
            val map = hashMapOf<String,Any>(
                "expense" to expense,
            )
            if (expenseStatus) {
                firestore.collection(collections.userIncome)
                    .document(userUID)
                    .update(map)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveIncomeDetails(activityName)
                        }
                    }
            }else{
                if (incomeStatus){
                    firestore.collection(collections.userIncome)
                        .document(userUID)
                        .update(map)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                saveIncomeDetails(activityName)
                            }
                        }
                }else{
                    firestore.collection(collections.userIncome)
                        .document(userUID)
                        .set(map)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                saveIncomeDetails(activityName)
                            }
                        }
                }
            }
        }
    }

    private fun saveIncomeDetails(activityName: String) {
        val incomeID   =  firestore.collection(collections.userIncome).document().id
        var subCollection = ""
        var field = ""
        var value = ""

        if (activityName == "income"){
            subCollection = collections.userIncomeDetails
            field = "incomeDetails"
            value = binding.includeDateField.edIncome.text.toString()
        }

        if (activityName == "expense"){
            subCollection = collections.userExpenseDetails
            field = "expenseDetails"
            value = binding.includeDateField.edIncome.text.toString()
        }

        val map = hashMapOf<String,Any>(
            "amount" to value,
            "id" to incomeID,
            "date" to date,
            "time" to time,
            "category" to category,
            "paymentMethod" to paymentMethod,
            "notes" to notes
        )

        firestore.collection(collections.userIncome)
            .document(userUID)
            .collection(subCollection)
            .document(incomeID)
            .set(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this,"$field Save...",Toast.LENGTH_SHORT).show()
                     loadingBar.HideDialog()
                }
            }
    }

    private fun isValid(income : String, category : String, paymentMethod : String) : Boolean{

        if (income.isEmpty() || category.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(this,"Fill Required fields",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun observeData() {
        bottomSheetTaskModel.category.observe(this){value->
            binding.includeDateField.tvCategory.text = value
        }
        bottomSheetTaskModel.name.observe(this){value->
            binding.includeDateField.tvPaymentMethod.text = value.toString()
        }
        bottomSheetTaskModel.dialogStatus.observe(this){ value ->
                showPaymentMethodDialog(value)
        }
    }

    private fun showPaymentMethodDialog(value : String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_payment_dialog_layout)

        val ed_bankName  = dialog.findViewById(R.id.ed_bankName) as EditText
        val tv_cancel  = dialog.findViewById(R.id.tv_cancel) as TextView
        val tv_save = dialog.findViewById(R.id.tv_save) as TextView

        if (value == "payment") {
            ed_bankName.hint = "Bank Name"
        }else if (value == "category") {
            ed_bankName.hint = "Category"
        }

        tv_save.setOnClickListener {
            loadingBar.ShowDialog("please wait...")
           if (value == "payment") {
               saveBankCategory(ed_bankName.text.toString(),"banks")
           }else if (value == "category") {
               saveBankCategory(ed_bankName.text.toString(),"category")
           }
            dialog.dismiss()
        }

        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    }

    private fun saveBankCategory(name: String,collectionName : String) {
        val id : String = firestore.collection("users").document().id
        val map = hashMapOf<String,Any>(
            "name" to name.uppercase(Locale.ROOT),
            "id" to id,
        )

        firestore.collection("users")
            .document(userUID)
            .collection(collectionName)
            .document(id)
            .set(map)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    loadingBar.HideDialog()
                    Toast.makeText(this,"Save completed",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{ it->
                loadingBar.HideDialog()
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()

            }
    }

    private fun setCurrentDateTime() {
        date = currentDateTime.getCurrentDate().toString()
        time = currentDateTime.getTimeWithAmPm().toString()
        binding.includeDateField.tvDate.text = date
        binding.includeDateField.tvTime.text = time
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDateDialog(tv_date: TextView) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            OnDateSetListener { view, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val simpleDateFormat = SimpleDateFormat("dd/MMM/yyyy")
                tv_date.text = simpleDateFormat.format(calendar.time)
                date = tv_date.text.toString()

            }
        DatePickerDialog(
            this, dateSetListener,
            calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY,hourOfDay)
            set(Calendar.MINUTE,minute)
        }
        displayFormatTime(calendar.timeInMillis)
    }

    private fun displayFormatTime(timeStamp : Long){
        binding.includeDateField.tvTime.text = formatter.format(timeStamp)
    }


}