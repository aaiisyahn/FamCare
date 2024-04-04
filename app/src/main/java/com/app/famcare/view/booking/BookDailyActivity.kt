package com.app.famcare.view.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.app.famcare.R
import java.util.Calendar
import android.widget.TextView
import android.widget.AdapterView
import android.view.View
import android.widget.Button
import com.app.famcare.view.history.HistoryActivity

class BookDailyActivity : AppCompatActivity() {

    private lateinit var bookingDateEditText: EditText
    private lateinit var outputWorkingHours: TextView
    private lateinit var outputBookingDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_daily)

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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedWorkingHours = parent?.getItemAtPosition(position).toString()
                outputWorkingHours.text = "Working Hours   : $selectedWorkingHours"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val bookingNannyView = findViewById<Button>(R.id.buttonBookNanny)
        bookingNannyView.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                outputBookingDate.text = "Selected Booking Date: $selectedDate"
                bookingDateEditText.setText(selectedDate) // Optional: Update EditText text
            },
            // Set initial date:
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}