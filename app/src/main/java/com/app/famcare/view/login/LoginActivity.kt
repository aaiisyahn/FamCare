package com.app.famcare.view.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.databinding.ActivityLoginBinding
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

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
            // Log statement for debugging
            println("New user detected, scheduling verification popup")
            // Schedule the popup to be shown after a short delay
            Handler(Looper.getMainLooper()).postDelayed({
                showVerificationPopup()
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
                        }
                    } else {
                        Toast.makeText(this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show()
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

    private fun showVerificationPopup() {
        runOnUiThread {
            val inflater = LayoutInflater.from(this)
            val popupView = inflater.inflate(R.layout.activity_verification_popup, null)

            val popupWindow = PopupWindow(popupView, WRAP_CONTENT, WRAP_CONTENT, true)
            popupWindow.showAtLocation(binding.root, android.view.Gravity.CENTER, 0, 0)

           // val resendButton = popupView.findViewById<Button>(R.id.resendVerificationEmailButton)
            val dismissButton = popupView.findViewById<Button>(R.id.dismissButton)

          //  resendButton.setOnClickListener {
            //    firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
             //       if (task.isSuccessful) {
                    //    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
              //      } else {
              //          Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                   // }
              //  }
       //     }

            dismissButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }
}
