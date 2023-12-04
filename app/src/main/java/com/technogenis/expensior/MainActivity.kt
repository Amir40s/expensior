package com.technogenis.expensior

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.technogenis.expensior.constant.Collections
import com.technogenis.expensior.databinding.ActivityMainBinding
import com.technogenis.expensior.fragment.HomeFragment
import com.technogenis.expensior.fragment.ProfileFragment
import com.technogenis.expensior.home.ExpenseHistoryActivity
import com.technogenis.expensior.start.LoginActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding : ActivityMainBinding

    private lateinit var auth: FirebaseAuth


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setSupportActionBar(binding.toolbar)


        binding.navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }

    }

    override
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
                binding.toolbar.title = "Expensior"
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
                binding.toolbar.title = "Profile"
            }
            R.id.nav_debit -> {
                moveToNext(Collections().userExpenseDetails)
            }
            R.id.nav_credit ->  moveToNext(Collections().userIncomeDetails)
            R.id.nav_chat -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_logout -> signOut()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun moveToNext(value: String) {
        val intent = Intent(this,ExpenseHistoryActivity::class.java)
        intent.putExtra("type",value)
        startActivity(intent)
    }

    private fun signOut() {
        val user = auth.currentUser?.uid
        if (user!=null)
        {
            auth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}