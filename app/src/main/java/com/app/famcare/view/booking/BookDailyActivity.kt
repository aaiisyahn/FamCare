package com.app.famcare.view.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import com.app.famcare.view.history.HistoryActivity
import java.util.Calendar
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar

class BookDailyActivity : AppCompatActivity() {

    private lateinit var bookingDateEditText: EditText
    private lateinit var outputWorkingHours: TextView
    private lateinit var outputBookingDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_daily)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val workingHoursSpinner = findViewById<Spinner>(R.id.workingHoursSpinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.working_hours_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        workingHoursSpinner.adapter = adapter

        bookingDateEditText = findViewById<EditText>(R.id.bookingDateEditText)
        outputWorkingHours = findViewById<TextView>(R.id.outputWorkingHours)
        outputBookingDate = findViewById<TextView>(R.id.outputBookingDate)

        bookingDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        workingHoursSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedWorkingHours = parent?.getItemAtPosition(position).toString()
                outputWorkingHours.text = "Working Hours   : $selectedWorkingHours"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val buttonBookNanny = findViewById<Button>(R.id.buttonBookNanny)
        buttonBookNanny.setOnClickListener {
            showBookingSuccessDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                outputBookingDate.text = "Booking Date      : $selectedDate"
                bookingDateEditText.setText(selectedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showBookingSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Booking Success")
        builder.setMessage("Your booking has been successfully made.")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            dialogInterface.dismiss()
        }
        builder.show()
    }
}