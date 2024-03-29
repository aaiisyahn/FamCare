package com.app.famcare.adapter

import com.app.famcare.R
import java.util.ArrayList

object DataNanny {
    private val nameNanny = arrayOf(
        "Ahmad Setiawan",
        "Dewi Lestari",
        "Putri Utami",
        "Siti Rahayu",
        "Joko Santoso",
        "Budi Prasetyo",
        "Rini Susanti",
        "Mega Wulandari",
        "Eka Putri",
        "Adi Nugroho"
    )

    private val genderNanny = arrayOf(
        "male", "female", "female", "female", "male", "male", "female", "female", "female", "male"
    )

    private val ageNanny = arrayOf(
        25, 28, 30, 32, 27, 29, 31, 26, 33, 30
    )

    private val experienceNanny = arrayOf(
        "> 5 years",
        "> 5 years",
        "< 5 years",
        "> 5 years",
        "< 5 years",
        "> 5 years",
        "> 5 years",
        "< 5 years",
        "> 5 years",
        "< 5 years"
    )

    private val salaryNanny = arrayOf(
        ">Rp5.000.000,00",
        "<Rp5.000.000,00",
        ">Rp5.000.000,00",
        "<Rp5.000.000,00",
        ">Rp5.000.000,00",
        "<Rp5.000.000,00",
        ">Rp5.000.000,00",
        "<Rp5.000.000,00",
        ">Rp5.000.000,00",
        "<Rp5.000.000,00"
    )

    private val skillNanny = arrayOf(
        listOf("cooking", "cleaning", "child care"),
        listOf("child care", "first aid"),
        listOf("child care", "housekeeping"),
        listOf("cooking", "child care"),
        listOf("cleaning", "child care"),
        listOf("cooking", "cleaning"),
        listOf("child care", "housekeeping"),
        listOf("cooking", "child care"),
        listOf("cleaning", "child care"),
        listOf("child care", "housekeeping")
    )

    private val typeNanny = arrayOf(
        "daily",
        "monthly",
        "daily",
        "monthly",
        "daily",
        "monthly",
        "daily",
        "monthly",
        "daily",
        "monthly"
    )

    private val rateNanny = arrayOf(
        4.75, 4.5, 4.25, 4.0, 4.75, 4.25, 4.0, 4.5, 4.75, 4.25
    )

    private val contactNanny = arrayOf(
        "081234567891",
        "082345678912",
        "083456789123",
        "084567891234",
        "085678912345",
        "086789123456",
        "087891234567",
        "088912345678",
        "089123456789",
        "081234567890"
    )

    private val pictNanny = arrayOf(
        R.drawable.nanny_placeholder_male,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder_male,
        R.drawable.nanny_placeholder_male,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder,
        R.drawable.nanny_placeholder_male
    )

    private val locationNanny = arrayOf(
        "Jakarta",
        "Surabaya",
        "Bandung",
        "Medan",
        "Semarang",
        "Makassar",
        "Palembang",
        "Depok",
        "Tangerang",
        "Bekasi"
    )

    fun getListNanny(): ArrayList<Nanny> {
        val list = ArrayList<Nanny>()
        for (position in nameNanny.indices) {
            val nanny = Nanny(
                nameNanny[position],
                genderNanny[position],
                ageNanny[position],
                experienceNanny[position],
                salaryNanny[position],
                skillNanny[position],
                typeNanny[position],
                rateNanny[position],
                contactNanny[position],
                pictNanny[position],
                locationNanny[position]
            )
            list.add(nanny)
        }
        return list
    }
}
