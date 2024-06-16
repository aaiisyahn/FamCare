package com.app.famcare.view.onboarding


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.view.register.RegisterActivity


class ThirdOnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_onboarding)
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@ThirdOnboardingActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}