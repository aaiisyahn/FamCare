package com.app.famcare.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.app.famcare.R
import com.app.famcare.adapter.DataNanny
import com.app.famcare.adapter.NannyAdapter
import com.app.famcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val nannyList = DataNanny.getListNanny()

        val adapter = NannyAdapter(this, nannyList)

        binding.recyclerViewNanny.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewNanny.adapter = adapter
    }
}