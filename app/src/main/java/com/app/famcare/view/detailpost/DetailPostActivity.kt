package com.app.famcare.view.detailpost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.adapter.Nanny
import com.app.famcare.databinding.ActivityDetailPostBinding

class DetailPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPostBinding
    private lateinit var nanny: Nanny

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        nanny = intent.getParcelableExtra<Nanny>("nanny") ?: return

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Nanny's Detail"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        with(binding) {
            imageViewGender.setImageResource(
                if (nanny.gender == "male") com.app.famcare.R.drawable.ic_male
                else com.app.famcare.R.drawable.ic_female
            )

            imageViewNanny.setImageResource(nanny.pict)
            textViewName.text = nanny.name

            val ageString = "${nanny.age} years old"
            textViewAge.text = ageString

            val rateString = "${nanny.rate}"
            textViewRate.text = rateString

            val typeString = "${nanny.type}"
            textViewType.text = typeString

            val experienceString = "${nanny.experience} experiences"
            textViewExperience.text = experienceString

            textViewLocation.text = nanny.location

            val salaryString = "${nanny.salary}"
            textViewSalary.text = salaryString

            val skillsString = "${nanny.skills}"
            textViewSkills.text = skillsString
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}