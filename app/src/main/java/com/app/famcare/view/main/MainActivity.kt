package com.app.famcare.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.famcare.R
import com.app.famcare.adapter.NannyAdapter
import com.app.famcare.databinding.ActivityMainBinding
import com.app.famcare.repository.NannyRepository
import com.app.famcare.view.detailpost.DetailPostActivity
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.view.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), FilterFragment.FilterListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NannyAdapter
    private val nannyRepository = NannyRepository()
    private var isGrid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupRecyclerView(isGrid)

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

        // Fetch data when the activity starts
        fetchData()

        binding.imageViewFilter.setOnClickListener {
            val filterFragment = FilterFragment()
            filterFragment.setFilterListener(this)
            filterFragment.show(supportFragmentManager, "FilterFragment")
        }

        binding.listOption.setOnClickListener {
            isGrid = !isGrid
            setupRecyclerView(isGrid)
        }
    }

    private fun fetchData() {
        adapter.fetchData()
    }

    private fun setupRecyclerView(isGrid: Boolean) {
        val layoutManager = if (isGrid) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
        binding.recyclerViewNanny.layoutManager = layoutManager

        // Update the icon based on the current layout
        binding.listOption.setImageResource(if (isGrid) R.drawable.ic_list else R.drawable.ic_grid)

        val layoutResource = if (isGrid) {
            R.layout.item_row_nanny
        } else {
            R.layout.item_list_nanny
        }

        // Initialize adapter with new layoutResource if necessary
        if (!::adapter.isInitialized || adapter.layoutResource != layoutResource) {
            adapter = NannyAdapter(this, layoutResource) { nannyId ->
                val intent = Intent(this, DetailPostActivity::class.java)
                intent.putExtra("nannyId", nannyId)
                startActivity(intent)
            }
            binding.recyclerViewNanny.adapter = adapter
        } else {
            adapter.updateLayoutResource(layoutResource)
        }

        fetchData()
    }

    override fun onFilterApplied(filterCriteria: Map<String, Any>) {
        adapter.applyFilter(filterCriteria)
    }
}
