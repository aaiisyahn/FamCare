package com.app.famcare.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.app.famcare.R
import com.app.famcare.adapter.DataNanny
import com.app.famcare.adapter.NannyAdapter
import com.app.famcare.databinding.ActivityMainBinding
import com.app.famcare.view.chat.ChatActivity
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.view.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val nannyList = DataNanny.getListNanny()

        val adapter = NannyAdapter(this, nannyList)

        binding.recyclerViewNanny.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewNanny.adapter = adapter

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.page_1

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
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
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}