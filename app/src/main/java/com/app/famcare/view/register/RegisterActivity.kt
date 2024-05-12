package com.app.famcare.view.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.famcare.R
import com.app.famcare.databinding.ActivityLoginBinding
import com.app.famcare.databinding.ActivityRegisterBinding
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // Tambahkan variabel firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Inisialisasi Firestore

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { createUserTask ->
                    if (createUserTask.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val uid = user?.uid

                        // Jika UID berhasil diperoleh, simpan data ke Firestore
                        uid?.let { uid ->
                            saveUserDataToFirestore(uid, email) // Panggil fungsi untuk menyimpan data ke Firestore
                        }

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, createUserTask.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bottom2TextViews.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserDataToFirestore(uid: String, email: String) {
        // Mendefinisikan data pengguna yang akan disimpan di Firestore
        val userData = hashMapOf(
            "email" to email
            // Anda bisa menambahkan data pengguna lainnya di sini, seperti nama lengkap atau nomor telepon
        )

        // Menyimpan data pengguna ke Firestore
        firestore.collection("famcare_user").document(uid)
            .set(userData)
            .addOnSuccessListener {
                // Data pengguna berhasil disimpan di Firestore
            }
            .addOnFailureListener { exception ->
                // Gagal menyimpan data pengguna di Firestore
                Log.e("RegisterActivity", "Error writing document", exception)
            }
    }
}
