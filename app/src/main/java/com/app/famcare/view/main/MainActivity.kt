package com.app.famcare.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.famcare.R
import androidx.recyclerview.widget.GridLayoutManager
import com.app.famcare.adapter.NannyAdapter
import com.app.famcare.databinding.ActivityMainBinding
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.profile.ProfileActivity
import com.app.famcare.view.main.FilterFragment
import androidx.fragment.app.FragmentTransaction
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.repository.NannyRepository
import com.app.famcare.view.detailpost.DetailPostActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), FilterFragment.FilterListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NannyAdapter
    private val nannyRepository = NannyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = NannyAdapter(this) { nannyId ->
            val intent = Intent(this, DetailPostActivity::class.java)
            intent.putExtra("nannyId", nannyId)
            startActivity(intent)
        }

        binding.recyclerViewNanny.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewNanny.adapter = adapter

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.page_1

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> true
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

        // Panggil fetchData() saat aktivitas dimulai
        fetchData()

        binding.imageViewFilter.setOnClickListener {
            val filterFragment = FilterFragment()
            filterFragment.setFilterListener(this) // Set the listener
            filterFragment.show(supportFragmentManager, "FilterFragment")
        }
    }

    private fun fetchData() {
        adapter.fetchData()
    }

    override fun onFilterApplied(filterCriteria: Map<String, Any>) {
        // Handle filter criteria here
        adapter.applyFilter(filterCriteria)
    }
}