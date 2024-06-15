package com.app.famcare.view.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.databinding.ActivityLoginBinding
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

        val isNewUser = intent.getBooleanExtra("isNewUser", false)
        if (isNewUser) {
            println("New user detected, scheduling verification dialog")
            Handler(Looper.getMainLooper()).postDelayed({
                showVerificationDialog()
            }, 500)
        }

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

        binding.bottom2TextViews.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset all TextViews to empty
        resetTextViews()
    }

    private fun resetTextViews() {
        binding.emailEditText.text?.clear()
        binding.passwordEditText.text?.clear()
    }

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

    private fun showVerificationDialog() {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Email Verification")
            builder.setMessage("Please verify your email address.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
