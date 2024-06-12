package com.app.famcare.view.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.app.famcare.R
import com.app.famcare.databinding.ActivityProfileBinding
import com.app.famcare.view.bookmark.BookmarkActivity
import com.app.famcare.view.chat.ChatActivity
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.maps.DaycareMapsActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var textPhone: TextView
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val bottomNavigation: BottomNavigationView = binding.bottomNavigation
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
                    true
                }

                else -> false
            }
        }

        val editProfileCardView: CardView = binding.editProfile
        editProfileCardView.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        val settingsLayout: LinearLayout = binding.accountSettings
        settingsLayout.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        val bookmarkLayout: LinearLayout = binding.bookmarkActivity
        bookmarkLayout.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }
        val termsofserviceLayout: LinearLayout = binding.termsOfServices
        termsofserviceLayout.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        val privacypolicyLayout: LinearLayout = binding.privacyPolicy
        privacypolicyLayout.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        val aboutLayout: LinearLayout = binding.aboutPage
        aboutLayout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        val logoutCardView: LinearLayout = binding.logoutButton
        logoutCardView.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                // User clicked OK button
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                // User cancelled the dialog
                dialog.dismiss()
            })
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        loadUserDataFromFirestore()
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

    private fun loadUserDataFromFirestore() {
        val currentUserUid = firebaseAuth.currentUser?.uid

        if (currentUserUid != null) {
            firestore.collection("User").document(currentUserUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("fullName")
                        val email = document.getString("email")
                        val phone = document.getString("phone")

                        binding.textName.text = name
                        binding.textEmail.text = email
                        binding.textPhone.text = phone

                        val imageUrl = document.getString("profileImageUrl")
                        if (imageUrl != null && imageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .into(binding.imageprofile)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}
