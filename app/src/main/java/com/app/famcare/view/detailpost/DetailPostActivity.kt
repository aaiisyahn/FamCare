package com.app.famcare.view.detailpost

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.model.Nanny
import com.app.famcare.databinding.ActivityDetailPostBinding
import com.app.famcare.view.booking.BookDailyActivity
import com.app.famcare.view.chat.ChatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var nannyId: String
    private var nanny: Nanny? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan id Nanny dari intent
        nannyId = intent.getStringExtra("nannyId") ?: return

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Mengambil data Nanny dari Firestore
        FirebaseFirestore.getInstance().collection("Nanny").document(nannyId).get()
            .addOnSuccessListener { document ->
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
                            textViewRateDP.text = "${nannyData.rate}"
                            textViewTypeDP.text = nannyData.type
                            textViewExperienceDP.text = "${nannyData.experience} experiences"
                            textViewLocationDP.text = nannyData.location
                            textViewSalaryDP.text = nannyData.salary
                            textViewSkillsDP.text = nannyData.skills.joinToString(", ")
                        }
                    } ?: run {
                        Log.e("DetailPostActivity", "Nanny object is null")
                    }
                } else {
                    Log.e("DetailPostActivity", "Document does not exist")
                }
            }.addOnFailureListener { exception ->
                Log.e("DetailPostActivity", "Error getting document: ", exception)
            }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Mengubah buttonContactNanny dan imageViewContact menjadi Button
        val chatNannyButton = findViewById<Button>(R.id.imageViewContact)
        chatNannyButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        val bookNannyButton = findViewById<Button>(R.id.buttonContactNanny)
        bookNannyButton.setOnClickListener {
            val intent = Intent(this, BookDailyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}