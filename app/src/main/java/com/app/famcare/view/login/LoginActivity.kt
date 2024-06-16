package com.app.famcare.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.databinding.ActivityLoginBinding
import com.app.famcare.view.customview.EmailEditText
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Pengaturan listener untuk tombol Login
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null && currentUser.isEmailVerified) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                            firebaseAuth.signOut() // Sign out the user if email is not verified
                        }
                    } else {
                        handleLoginError(task.exception)
                    }
                }
            } else {
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Pengaturan listener untuk tombol Register
        binding.bottom2TextViews.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Pengaturan listener untuk tombol Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordPopup()
        }
    }

    // Fungsi untuk menangani error saat login
    private fun handleLoginError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showForgotPasswordPopup() {
        val dialogView = layoutInflater.inflate(R.layout.popup_reset_password, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        // Mendapatkan referensi ke komponen-komponen dalam popup
        val emailEditText = dialogView.findViewById<EmailEditText>(R.id.emailEditText)
        val resetPasswordButton = dialogView.findViewById<Button>(R.id.resetPasswordButton)
        val bottom2TextViews = dialogView.findViewById<TextView>(R.id.bottom2TextViews)

        // Pengaturan listener untuk tombol "Submit" di dalam popup
        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        // Pengaturan listener untuk tombol "Back to Login" di dalam popup
        bottom2TextViews.setOnClickListener {
            dialog.dismiss()
        }
    }
}
