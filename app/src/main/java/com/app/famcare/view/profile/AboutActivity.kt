package com.app.famcare.view.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.famcare.R
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.app.famcare.view.bookmark.BookmarkActivity
import com.app.famcare.view.main.MainActivity

import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

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