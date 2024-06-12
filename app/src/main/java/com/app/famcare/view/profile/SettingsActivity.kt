package com.app.famcare.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var resetPasswordLayout: LinearLayout
    private lateinit var passwordTextView: TextView
    private lateinit var currentPasswordTextLayout: LinearLayout
    private lateinit var currentPasswordEditText: EditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        firebaseAuth = FirebaseAuth.getInstance()

        resetPasswordLayout = findViewById(R.id.resetPassword)
        passwordTextView = findViewById(R.id.passwordTextView)
        currentPasswordTextLayout = findViewById(R.id.currentPasswordTextLayout)
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        resetButton = findViewById(R.id.resetButton)

        resetPasswordLayout.setOnClickListener {
            passwordTextView.visibility = View.VISIBLE
            currentPasswordTextLayout.visibility = View.VISIBLE
            resetButton.visibility = View.VISIBLE
        }

        resetButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            if (currentPassword.isNotEmpty()) {
                verifyCurrentPassword(currentPassword)
            } else {
                showAlertDialog("Error", "Please enter your current password.")
            }
        }
    }

    private fun verifyCurrentPassword(currentPassword: String) {
        val user = firebaseAuth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendPasswordResetEmail(user.email!!)
                    } else {
                        showAlertDialog("Error", "Current password is incorrect.")
                    }
                }
        }
    }

    private fun sendPasswordResetEmail(userEmail: String) {
        firebaseAuth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showAlertDialog("Success", "Password reset email sent.") {
                        restartActivity()
                    }
                } else {
                    showAlertDialog("Error", "Failed to send reset email.")
                }
            }
    }

    private fun showAlertDialog(title: String, message: String, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                onDismiss?.invoke()
            }
            .show()
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}
