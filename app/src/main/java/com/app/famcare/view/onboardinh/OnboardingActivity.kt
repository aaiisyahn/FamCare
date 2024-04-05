package com.app.famcare.view.onboardinh

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.register.RegisterActivity


class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        loginButton.setOnClickListener { // Logic untuk mengarahkan ke halaman login
            val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener { // Logic untuk mengarahkan ke halaman register
            val intent = Intent(this@OnboardingActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
