package com.app.famcare.view.bookmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.app.famcare.R
import com.app.famcare.adapter.BookmarkAdapter
import com.app.famcare.databinding.ActivityBookmarkBinding
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var adapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = BookmarkAdapter(this)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 1)
        binding.recyclerView.adapter = adapter

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
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