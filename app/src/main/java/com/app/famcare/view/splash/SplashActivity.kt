package com.app.famcare.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.view.login.LoginActivity
import com.app.famcare.view.onboardinh.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}



