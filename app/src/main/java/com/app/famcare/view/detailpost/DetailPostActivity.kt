package com.app.famcare.view.detailpost

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.model.Nanny
import com.app.famcare.databinding.ActivityDetailPostBinding
import com.app.famcare.view.booking.BookDailyActivity
import com.app.famcare.view.chat.ChatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.Toast
import com.app.famcare.view.booking.BookMonthlyActivity

class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var nannyId: String
    private var nanny: Nanny? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan id Nanny dari intent
        nannyId = intent.getStringExtra("nannyId") ?: run {
            Toast.makeText(this, "No nanny ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        loadNannyDataFromFirestore()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val bookNannyButton = findViewById<Button>(R.id.buttonBookNanny)
        bookNannyButton.setOnClickListener {
            // Kirim data Nanny yang dipilih ke BookDailyActivity atau BookMonthlyActivity
            val intent = when (nanny?.type) {
                "daily" -> Intent(this, BookDailyActivity::class.java)
                "monthly" -> Intent(this, BookMonthlyActivity::class.java)
                else -> {
                    Toast.makeText(this, "Invalid nanny type", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            intent.putExtra("nannyId", nannyId)
            intent.putExtra("activityName", this::class.java.simpleName) // Untuk mengetahui activity asal
            startActivity(intent)
        }


    }

    private fun loadNannyDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Nanny").document(nannyId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                nanny = document.toObject(Nanny::class.java)

                nanny?.let { nannyData ->
                    with(binding) {
                        // Menggunakan Glide untuk memuat gambar dari URL
                        Glide.with(this@DetailPostActivity).load(nannyData.pict)
                            .placeholder(R.drawable.placeholder_image).into(imageViewNannyDP)

                        imageViewGenderDP.setImageResource(
                            if (nannyData.gender == "male") R.drawable.ic_male else R.drawable.ic_female
                        )

                        textViewNameDP.text = nannyData.name
                        textViewAgeDP.text = "${nannyData.age} years old"
                        textViewRateDP.text = nannyData.rate
                        textViewTypeDP.text = nannyData.type
                        textViewExperienceDP.text = "${nannyData.experience} experiences"
                        textViewLocationDP.text = nannyData.location
                        textViewSalaryDP.text = nannyData.salary

                        // Clear existing views in skillListLayout
                        skillListLayout.removeAllViews()

                        // Dynamically add skills as TextViews
                        nannyData.skills.forEach { skill ->
                            val textView = TextView(this@DetailPostActivity)
                            textView.text = "• $skill"
                            textView.setTextColor(resources.getColor(android.R.color.black))
                            textView.textSize = 16f
                            skillListLayout.addView(textView) // Add the TextView to skillListLayout
                        }
                    }
                } ?: run {
                    Log.e("DetailPostActivity", "Nanny object is null")
                    Toast.makeText(this, "Failed to load nanny details", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("DetailPostActivity", "Document does not exist")
                Toast.makeText(this, "Nanny data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("DetailPostActivity", "Error getting document: ", exception)
            Toast.makeText(this, "Failed to load nanny data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}