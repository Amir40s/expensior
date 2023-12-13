package com.technogenis.expensior.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.R
import com.technogenis.expensior.bottomsheet.PaymentMethodBottomSheetFragment
import com.technogenis.expensior.bottomsheet.ViewTaskModel
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.constant.CurrentDateTime
import com.technogenis.expensior.constant.LoadingBar
import com.technogenis.expensior.databinding.ActivityTransferBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransferActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    // binding variable
    private lateinit var binding: ActivityTransferBinding

    // data store
    private lateinit var userUID : String
    private  var amount : String? = null
    private lateinit var to : String
    private lateinit var from : String
    private lateinit var date : String
    private lateinit var time : String
    private var notes : String = ""
    private lateinit var transferData : String
    private lateinit var transfer : String

    private var loadingBar = LoadingBar(this)
    private var currentDateTime =  CurrentDateTime(this)
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("hh:mm a", Locale.US)
    private  var collections = Collections()
    private lateinit var bottomSheetTaskModel: ViewTaskModel

    private  var expenseFound : Boolean = false
    private  var transferStatus : Boolean = false

    //firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onStart() {
        super.onStart()
//        loadingBar.ShowDialog("please wait")
        setCurrentDateTime()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetTaskModel = ViewModelProvider(this)[ViewTaskModel::class.java]

        firebaseAuth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userUID = firebaseAuth.currentUser?.uid.toString()

        // functions
        observeData()

        binding.topLayout.backText.text = "Transfer"
        binding.topLayout.backImage.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.dataFieldLayout.tvTime.setOnClickListener {
            TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.dataFieldLayout.tvDate.setOnClickListener{
            showDateDialog(binding.dataFieldLayout.tvDate)
        }

        binding.dataFieldLayout.tvToBank.setOnClickListener {
            PaymentMethodBottomSheetFragment("to").show(supportFragmentManager,"paymentSheetTag")
        }
        binding.dataFieldLayout.tvFromBank.setOnClickListener {
            PaymentMethodBottomSheetFragment("from").show(supportFragmentManager,"paymentSheetTag")
        }

        binding.dataFieldLayout.btnSave.setOnClickListener {

            amount = binding.dataFieldLayout.edAmount.text.toString()
            to = binding.dataFieldLayout.tvToBank.text.toString()
            from = binding.dataFieldLayout.tvFromBank.text.toString()
            date = binding.dataFieldLayout.tvDate.text.toString()
            time = binding.dataFieldLayout.tvTime.text.toString()
            notes = binding.dataFieldLayout.tvNotes.text.toString()

            if (isValid(amount!!,to,from,date,time))
            {
                loadingBar.ShowDialog("Please wait")
                getTransferDetails()
            }

        }


    }

    private fun getTransferDetails() {

        firestore.collection(collections.userIncome)
            .document(userUID)
            .get()
            .addOnCompleteListener{ snapShot ->
                if (snapShot.isSuccessful)
                {
                    val document = snapShot.result

                    if (document.contains("income${currentDateTime.getCurrentMonth().toString().lowercase()}"))
                    {
                        expenseFound = true
                    }
                    if (document.contains("expense${currentDateTime.getCurrentMonth().toString().lowercase()}"))
                    {
                        expenseFound = true
                    }
                    if (document.contains("transfer${currentDateTime.getCurrentMonth().toString().lowercase()}"))
                    {
                        transferData =  document.get("transfer${currentDateTime.getCurrentMonth().toString().lowercase()}").toString()
                        transferStatus = true
                        transfer = (transferData.toInt() + amount!!.toInt()).toString()
                    }else{
                        transferData = "0"
                        transferStatus = false
                        transfer = (transferData.toInt() + amount!!.toInt()).toString()
                    }


                    amount?.let { saveData(it) }
                }
            }.addOnFailureListener{
                Toast.makeText(this,"something went wrong...",Toast.LENGTH_SHORT).show()
                loadingBar.HideDialog()
            }
    }

    private fun saveData(transfer: String) {

        if (expenseFound){
            updateData(transfer)
        }else{
            saveFirestoreData(transfer)
        }


    }

   fun saveFirestoreData(transfer: String){
       val map = hashMapOf<String, Any>(
           "transfer${currentDateTime.getCurrentMonth().toString().lowercase()}" to transfer
       )
       firestore.collection(collections.userIncome)
           .document(userUID)
           .set(map)
           .addOnCompleteListener {
               if (it.isSuccessful) {
                   saveIncomeDetails(transfer)
               }
           }
    }

    fun updateData(transfer: String){
        val map = hashMapOf<String, Any>(
            "transfer${currentDateTime.getCurrentMonth().toString().lowercase()}" to transfer
        )
        firestore.collection(collections.userIncome)
            .document(userUID)
            .update(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    saveIncomeDetails(transfer)
                }
            }
    }

    private fun saveIncomeDetails(transfer: String) {

        val incomeID   =  firestore.collection(collections.userIncome).document().id

        val map = hashMapOf<String,Any>(
            "amount" to transfer,
            "id" to incomeID,
            "date" to date,
            "time" to time,
            "toBank" to to,
            "fromBank" to from,
            "notes" to notes,
            "month" to currentDateTime.getCurrentMonth().toString().lowercase()
        )

        firestore.collection(collections.userIncome)
            .document(userUID)
            .collection("transferDetails")
            .document(incomeID)
            .set(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this,"Transfer completed...",Toast.LENGTH_SHORT).show()
                    loadingBar.HideDialog()
                }
            }
    }

    private fun observeData() {
        bottomSheetTaskModel.bankCheck.observe(this){value->
            if (value == "to"){
                bottomSheetTaskModel.toName.observe(this){ name->
                    binding.dataFieldLayout.tvToBank.text = name.toString()
                }
            }else if (value == "from"){
                bottomSheetTaskModel.fromName.observe(this){ name->
                    binding.dataFieldLayout.tvFromBank.text = name.toString()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDateDialog(tv_date: TextView) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
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

    private fun isValid(amount: String, to: String, from: String,
                        date: String, time: String): Boolean {

        if (amount.isEmpty() || to.isEmpty() || from.isEmpty() || date.isEmpty() || time.isEmpty())
        {
            Toast.makeText(this,"Please fill following details",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setCurrentDateTime() {
        date = currentDateTime.getCurrentDate().toString()
        time = currentDateTime.getTimeWithAmPm().toString()
        binding.dataFieldLayout.tvDate.text = date
        binding.dataFieldLayout.tvTime.text = time
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY,hourOfDay)
            set(Calendar.MINUTE,minute)
        }
        displayFormatTime(calendar.timeInMillis)
    }
    private fun displayFormatTime(timeStamp : Long){
        binding.dataFieldLayout.tvTime.text = formatter.format(timeStamp)
    }
}