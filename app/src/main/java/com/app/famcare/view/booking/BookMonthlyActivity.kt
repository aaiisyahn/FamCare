package com.app.famcare.view.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.famcare.R
import com.app.famcare.model.Nanny
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.view.historyimport.HistoryBMFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BookMonthlyActivity : AppCompatActivity() {
    private lateinit var startDateEditText: EditText
    private lateinit var outputBookingDuration: TextView
    private lateinit var outputStartDate: TextView
    private lateinit var outputEndDate: TextView
    private lateinit var nannyId: String
    private lateinit var auth: FirebaseAuth
    private var selectedBookingDuration: Int = 0
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_monthly)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        auth = FirebaseAuth.getInstance()

        // Mendapatkan nannyId dari intent
        nannyId = intent.getStringExtra("nannyId") ?: ""

        // Memuat data Nanny dari Firestore
        loadNannyDataFromFirestore(nannyId)

        // Inisialisasi UI
        val bookingDurationSpinner = findViewById<Spinner>(R.id.bookingDurationSpinner)
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.booking_duration, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bookingDurationSpinner.adapter = adapter

        startDateEditText = findViewById(R.id.startDateEditText)
        outputBookingDuration = findViewById(R.id.outputBookingDuration)
        outputStartDate = findViewById(R.id.outputStartDate)
        outputEndDate = findViewById(R.id.outputEndDate)

        startDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        outputBookingDuration = findViewById(R.id.outputBookingDuration)
        outputBookingDuration.text = ""

        bookingDurationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
                ) {
                    selectedBookingDuration = parent?.getItemAtPosition(position).toString().toInt()
                    outputBookingDuration.text = "Booking Duration  : $selectedBookingDuration"

                    // Hitung end date jika start date sudah dipilih
                    if (outputStartDate.text.isNotEmpty()) {
                        calculateEndDate()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    outputBookingDuration.text = ""  // Kosongkan jika tidak ada yang dipilih
                }
            }

        val buttonBookNanny = findViewById<Button>(R.id.buttonBookNanny)
        buttonBookNanny.setOnClickListener {
            bookNanny()
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
        val calendar = Calendar.getInstance()

        fetchBookedDates { bookedDatesRanges ->
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val selectedDate = sdf.format(selectedCalendar.time)

                    if (isDateBooked(selectedCalendar.time, bookedDatesRanges)) {
                        showDateAlreadyBookedDialog()
                    } else {
                        outputStartDate.text = "Start Date                 : $selectedDate"
                        startDateEditText.setText(selectedDate)

                        // Calculate end date when start date is selected
                        calculateEndDate()
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Set minimum selectable date to tomorrow
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.show()
        }
    }

    private fun showDateAlreadyBookedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Date Already Booked")
            .setMessage("This date is already booked. Please select another date.")
            .setPositiveButton("OK") { dialog, which ->
                // Dismiss dialog if needed
            }
            .show()
    }

    private fun fetchBookedDates(callback: (List<Pair<Date, Date>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val bookingsRef = db.collection("BookingMonthly")

        bookingsRef.whereEqualTo("nannyID", nannyId)
            .get()
            .addOnSuccessListener { documents ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val bookedDatesRanges = mutableListOf<Pair<Date, Date>>()
                for (document in documents) {
                    val existingStartDate = document.getString("startDate") ?: ""
                    val existingEndDate = document.getString("endDate") ?: ""
                    val existingStartDateDate = sdf.parse(existingStartDate)
                    val existingEndDateDate = sdf.parse(existingEndDate)
                    bookedDatesRanges.add(existingStartDateDate to existingEndDateDate)
                }
                callback(bookedDatesRanges)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callback(emptyList())
            }
    }

    private fun isDateBooked(date: Date, bookedDatesRanges: List<Pair<Date, Date>>): Boolean {
        for ((startDate, endDate) in bookedDatesRanges) {
            // Check if the date falls within the booked range, inclusive of startDate and endDate
            if (!date.before(startDate) && !date.after(endDate)) {
                return true
            }
        }
        return false
    }

    private fun checkForExistingBookings(startDate: String, endDate: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val newStartDate = sdf.parse(startDate)
            val newEndDate = sdf.parse(endDate)

            val bookingsRef = db.collection("BookingMonthly")
            bookingsRef.whereEqualTo("nannyID", nannyId)
                .get()
                .addOnSuccessListener { documents ->
                    var isAvailable = true
                    for (document in documents) {
                        val existingStartDateStr = document.getString("startDate") ?: ""
                        val existingEndDateStr = document.getString("endDate") ?: ""
                        val existingStartDate = sdf.parse(existingStartDateStr)
                        val existingEndDate = sdf.parse(existingEndDateStr)

                        // Check if the new booking overlaps with existing bookings
                        if (newEndDate != existingStartDate && !newStartDate.equals(existingEndDate) && newStartDate.before(existingEndDate) && newEndDate.after(existingStartDate)) {
                            isAvailable = false
                            break
                        }
                    }
                    callback(isAvailable)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    callback(false)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing dates", e)
            callback(false)
        }
    }

    private fun bookNanny() {
        val selectedStartDate = startDateEditText.text.toString()
        val selectedEndDate = endDate  // Pastikan endDate sudah dihitung dan diset sebelumnya

        // Check for existing bookings before proceeding with the new booking
        checkForExistingBookings(selectedStartDate, selectedEndDate) { isAvailable ->
            if (isAvailable) {
                // Proceed with the booking
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Booking nanny...")
                progressDialog.show()

                // Construct the booking object
                val user = auth.currentUser
                val userId = user?.uid ?: ""

                val booking = hashMapOf(
                    "userID" to userId,
                    "nannyID" to nannyId,
                    "bookingDuration" to selectedBookingDuration.toString(),
                    "startDate" to selectedStartDate,
                    "endDate" to selectedEndDate,
                    "totalCost" to "Rp400.000,00/8 hours"
                )

                val db = FirebaseFirestore.getInstance()

                // Add booking to Firestore
                db.collection("BookingMonthly").add(booking)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                        // Update field bookID di dokumen Nanny
                        val nannyRef = db.collection("Nanny").document(nannyId)
                        nannyRef.update("bookID", documentReference.id)
                            .addOnSuccessListener {
                                Log.d(TAG, "bookID updated successfully")
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error updating bookID", e)
                            }

                        // Tambahkan bookID ke array bookIDs di dokumen User menggunakan arrayUnion
                        val userRef = db.collection("User").document(userId)
                        userRef.update("bookIDs", FieldValue.arrayUnion(documentReference.id))
                            .addOnSuccessListener {
                                Log.d(TAG, "bookID added to user document successfully")
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error updating user document with bookID", e)
                            }

                        progressDialog.dismiss()
                        // Tampilkan dialog sukses booking
                        showSuccessDialog()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed to book nanny", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Tampilkan pesan bahwa tanggal sudah dibooking
                Toast.makeText(
                    this,
                    "The selected date range is already booked for this nanny.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun calculateEndDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val startDateString = startDateEditText.text.toString()
        val calendar = Calendar.getInstance()

        try {
            val startDate = sdf.parse(startDateString)
            calendar.time = startDate
            calendar.add(Calendar.DAY_OF_MONTH, 30 * selectedBookingDuration)
            endDate = sdf.format(calendar.time)
            outputEndDate.text = "End Date                   : $endDate"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Booking Success")
        builder.setMessage("Your booking has been successfully made.")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("selectedTab", 1) // Mengirimkan informasi bahwa tab "Monthly" harus dipilih
            startActivity(intent)
            dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun loadNannyDataFromFirestore(nannyId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Nanny").document(nannyId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val nanny = document.toObject(Nanny::class.java)
                nanny?.let { nannyData ->
                    displayNannyInformation(nannyData)
                }
            } else {
                // Handle jika dokumen tidak ditemukan
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            // Handle kesalahan saat mengambil data dari Firestore
            Log.d(TAG, "get failed with ", exception)
        }
    }

    private fun displayNannyInformation(nanny: Nanny) {
        // Tampilkan informasi Nanny di layout
        val nameTextView = findViewById<TextView>(R.id.text_name)
        val typeTextView = findViewById<TextView>(R.id.text_email)
        val salaryTextView = findViewById<TextView>(R.id.text_salary)
        val profileImageView = findViewById<ImageView>(R.id.image_profile)

        nameTextView.text = nanny.name
        typeTextView.text = nanny.type
        salaryTextView.text = nanny.salary

        // Muat gambar Nanny ke ImageView menggunakan Glide
        Glide.with(this).load(nanny.pict).placeholder(R.drawable.nanny_placeholder)
            .error(R.drawable.placeholder_image).into(profileImageView)
    }

    companion object {
        private const val TAG = "BookMonthlyActivity"
    }

}