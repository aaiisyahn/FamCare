package com.app.famcare.view.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.app.famcare.R
import com.app.famcare.adapter.BookmarkAdapter
import com.app.famcare.adapter.DataNanny
import com.app.famcare.databinding.ActivityBookmarkBinding
import com.app.famcare.databinding.ActivityHistoryBinding
import com.app.famcare.view.chat.ChatActivity
import com.app.famcare.view.detailhistory.DetailHistoryActivity
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val seeHistoryView = findViewById<CardView>(R.id.bookingHistoryView)
        seeHistoryView.setOnClickListener {
            val intent = Intent(this, DetailHistoryActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.page_2

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.page_2 -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.page_3 -> {
                    val intent = Intent(this, FacilitiesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.page_4 -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}