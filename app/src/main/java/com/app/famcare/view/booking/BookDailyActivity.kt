package com.app.famcare.view.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.famcare.R
import com.app.famcare.model.Nanny
import com.app.famcare.view.history.HistoryActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BookDailyActivity : AppCompatActivity() {
    private lateinit var bookingDateEditText: EditText
    private lateinit var bookingDurationSpinner: Spinner
    private lateinit var startHourEditText: EditText
    private lateinit var outputBookingDate: TextView
    private lateinit var outputBookingDuration: TextView
    private lateinit var outputStartHour: TextView
    private lateinit var outputEndHour: TextView
    private lateinit var buttonBookNanny: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var nannyId: String
    private lateinit var db: FirebaseFirestore
    private lateinit var bookedHours: MutableSet<String>
    private var isDateSelected = false
    private var isStartHourSelected = false
    private var selectedDate: String = ""
    private var selectedDuration: String = ""
    private var selectedStartHour: String = ""
    private var selectedEndHour: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_daily)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nannyId = intent.getStringExtra("nannyId") ?: ""

        loadNannyDataFromFirestore(nannyId)

        bookingDateEditText = findViewById(R.id.bookingDateEditText)
        bookingDurationSpinner = findViewById(R.id.bookingDurationSpinner)
        startHourEditText = findViewById(R.id.startHourEditText)
        outputBookingDate = findViewById(R.id.outputBookingDate)
        outputBookingDuration = findViewById(R.id.outputBookingDuration)
        outputStartHour = findViewById(R.id.outputStartHour)
        outputEndHour = findViewById(R.id.outputEndHour)
        buttonBookNanny = findViewById(R.id.buttonBookNanny)

        bookingDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        val durationAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.working_hours_duration,
            android.R.layout.simple_spinner_item
        )
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bookingDurationSpinner.adapter = durationAdapter

        bookingDurationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDuration = parent?.getItemAtPosition(position).toString()
                outputBookingDuration.text = "Booking Duration   : $selectedDuration hours"
                if (selectedStartHour.isNotEmpty()) {
                    calculateEndHour(selectedDuration.substringBefore(" ").toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        startHourEditText.setOnClickListener {
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select date first", Toast.LENGTH_SHORT).show()
            } else {
                showStartTimePickerDialog()
            }
        }

        buttonBookNanny.setOnClickListener {
            if (validateInputs()) {
                // Periksa apakah sudah ada booking untuk nanny ini pada tanggal yang dipilih
                db.collection("BookingDaily")
                    .whereEqualTo("nannyID", nannyId)
                    .whereEqualTo("bookDate", selectedDate)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            // Jika ada booking, tampilkan pesan bahwa nanny sudah tidak tersedia
                            showBookingExistsDialog()
                        } else {
                            // Jika tidak ada booking, simpan data booking ke Firestore
                            saveBookingToFirestore()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error getting documents", e)
                        Toast.makeText(this, "Failed to check existing bookings", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                outputBookingDate.text = "Booking Date           : $selectedDate"
                bookingDateEditText.setText(selectedDate)
                resetOutputTimes() // Reset tampilan jam mulai dan jam selesai

                // Periksa apakah sudah ada booking untuk nanny ini pada tanggal yang dipilih
                db.collection("BookingDaily")
                    .whereEqualTo("nannyID", nannyId)
                    .whereEqualTo("bookDate", selectedDate)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            // Jika ada booking, tampilkan pesan bahwa nanny sudah tidak tersedia
                            showBookingExistsDialog()
                        }
                        // Tidak perlu else karena jika tidak ada booking, kita lanjutkan ke input berikutnya
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error getting documents", e)
                        Toast.makeText(this, "Failed to check existing bookings", Toast.LENGTH_SHORT).show()
                    }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun showStartTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                val endTimeCalendar = Calendar.getInstance()
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                endTimeCalendar.set(Calendar.MINUTE, selectedMinute)
                endTimeCalendar.add(Calendar.HOUR_OF_DAY, selectedDuration.substringBefore(" ").toInt())
                val endTime = endTimeCalendar.get(Calendar.HOUR_OF_DAY)

                if (selectedHour < 5 || selectedHour > 18 || (selectedHour == 18 && selectedMinute > 0)) {
                    // Jika waktu mulai tidak valid
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Invalid Time")
                    builder.setMessage("You cannot book at this time as the end time will exceed the working hours limit of 8:00 PM. Please select an earlier start time.")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        showStartTimePickerDialog() // Menampilkan kembali dialog jika waktu tidak valid
                    }
                    builder.show()
                } else {
                    selectedStartHour = selectedTime
                    startHourEditText.setText(selectedTime)
                    outputStartHour.text = "Start Time                 : $selectedStartHour"
                    calculateEndHour(selectedDuration.substringBefore(" ").toInt())
                }
            },
            hour,
            minute,
            true
        )

        timePickerDialog.setTitle("Select Start Time")
        timePickerDialog.show()
    }

    private fun calculateEndHour(durationHours: Int) {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        try {
            calendar.time = format.parse(selectedStartHour) ?: Date()
            calendar.add(Calendar.HOUR_OF_DAY, durationHours)
            val endTime = calendar.get(Calendar.HOUR_OF_DAY)
            val endMinute = calendar.get(Calendar.MINUTE)

            if (endTime >= 20 && endMinute > 0) {
                // Jika waktu selesai melewati 20:00
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Invalid Time")
                builder.setMessage("You cannot book at this time as the end time will exceed the working hours limit of 20.00. Please select an earlier start time.")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    // Reset waktu
                    selectedStartHour = ""
                    selectedEndHour = ""
                    startHourEditText.setText("")
                    outputStartHour.text = "Start Time                  :"
                    outputEndHour.text = "End Time                   :"
                }
                builder.show()
            } else {
                selectedEndHour = format.format(calendar.time)
                outputEndHour.text = "End Time                   : $selectedEndHour"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Error calculating end hour", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedDuration.isEmpty()) {
            Toast.makeText(this, "Please select booking duration", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedStartHour.isEmpty()) {
            Toast.makeText(this, "Please select start hour", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveBookingToFirestore() {
        val user = auth.currentUser
        val userId = user?.uid ?: ""

        // Data untuk disimpan ke Firestore
        val booking = hashMapOf(
            "userID" to userId,
            "nannyID" to nannyId,
            "bookDate" to selectedDate,
            "bookHours" to selectedStartHour,
            "endHours" to selectedEndHour,
            "bookDuration" to selectedDuration,
            "totalCost" to "Rp400.000,00/8 hours"
        )

        // Periksa apakah sudah ada booking untuk nanny ini pada tanggal yang dipilih
        db.collection("BookingDaily")
            .whereEqualTo("nannyID", nannyId)
            .whereEqualTo("bookDate", selectedDate)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Jika ada booking, tampilkan pesan bahwa nanny sudah tidak tersedia
                    showBookingExistsDialog()
                } else {
                    // Jika tidak ada booking, simpan data booking ke Firestore
                    db.collection("BookingDaily").add(booking)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                            // Update bookID di nanny document
                            val nannyRef = db.collection("Nanny").document(nannyId)
                            nannyRef.update("bookID", documentReference.id)
                                .addOnSuccessListener {
                                    Log.d(TAG, "bookID updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error updating bookID", e)
                                }

                            // Update bookIDs di user document
                            val userRef = db.collection("User").document(userId)
                            userRef.update(
                                "bookIDs", FieldValue.arrayUnion(documentReference.id)
                            ).addOnSuccessListener {
                                Log.d(TAG, "bookID added to user document successfully")
                                showSuccessDialog()
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error updating user document with bookID", e)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                            Toast.makeText(this, "Failed to book nanny", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents", e)
                Toast.makeText(this, "Failed to check existing bookings", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showBookingExistsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nanny Not Available")
        builder.setMessage("This nanny is already booked on the selected date. Please choose another date.")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun resetOutputTimes() {
        outputStartHour.text = "Start Hour                 :"
        outputEndHour.text = "End Time                  :"
        selectedStartHour = ""
        selectedEndHour = ""
    }

    private fun showSuccessDialog() {
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

    private fun loadNannyDataFromFirestore(nannyId: String) {
        val docRef = db.collection("Nanny").document(nannyId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val nanny = document.toObject(Nanny::class.java)
                nanny?.let { nannyData ->
                    displayNannyInformation(nannyData)
                }
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }

    private fun displayNannyInformation(nanny: Nanny) {
        val nameTextView = findViewById<TextView>(R.id.text_name)
        val typeTextView = findViewById<TextView>(R.id.text_email)
        val salaryTextView = findViewById<TextView>(R.id.text_salary)
        val profileImageView = findViewById<ImageView>(R.id.image_profile)

        nameTextView.text = nanny.name
        typeTextView.text = nanny.type
        salaryTextView.text = nanny.salary

        Glide.with(this).load(nanny.pict).placeholder(R.drawable.nanny_placeholder)
            .error(R.drawable.placeholder_image).into(profileImageView)
    }

    companion object {
        private const val TAG = "BookDailyActivity"
    }
}
