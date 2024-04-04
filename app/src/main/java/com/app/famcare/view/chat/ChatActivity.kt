package com.app.famcare.view.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.famcare.R
import com.app.famcare.databinding.ActivityChatBinding
import com.app.famcare.databinding.ActivityMainBinding

class ChatActivity : AppCompatActivity() {


    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chat)
    }
}
