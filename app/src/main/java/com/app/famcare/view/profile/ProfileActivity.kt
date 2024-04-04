package com.app.famcare.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.app.famcare.R
import com.app.famcare.view.bookmark.BookmarkActivity
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.maps.DaycareMapsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val usernameImageView = findViewById<ImageView>(R.id.image_profile)
        usernameImageView.setImageResource(R.drawable.user)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.page_4

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.page_2 -> {
                    val intent = Intent(this, BookmarkActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.page_3 -> {
                    val intent = Intent(this, DaycareMapsActivity::class.java)
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
        val editProfileCardView = findViewById<CardView>(R.id.editProfile)
        editProfileCardView.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val editBookmarkCardView = findViewById<CardView>(R.id.bookmarkActivity)
        editBookmarkCardView.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

        val viewToCCardView = findViewById<CardView>(R.id.termsOfServices)
        viewToCCardView.setOnClickListener {
            val intent = Intent(this, TermsOfServicesActivity::class.java)
            startActivity(intent)
        }

        val viewPrivacyPolicyCardView = findViewById<CardView>(R.id.privacyPolicy)
        viewPrivacyPolicyCardView.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        val editAboutCardView = findViewById<CardView>(R.id.aboutPage)
        editAboutCardView.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        val logoutCardView = findViewById<CardView>(R.id.logoutButton)
        logoutCardView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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