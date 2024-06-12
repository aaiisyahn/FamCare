package com.app.famcare.view.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.famcare.R
import com.app.famcare.view.history.HistoryActivity
import com.app.famcare.model.Nanny
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookDailyActivity : AppCompatActivity() {

    private lateinit var bookingDateEditText: EditText
    private lateinit var outputWorkingHours: TextView
    private lateinit var outputBookingDate: TextView
    private lateinit var nannyId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var workingHoursSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_daily)

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
        workingHoursSpinner = findViewById(R.id.workingHoursSpinner)
        bookingDateEditText = findViewById(R.id.bookingDateEditText)
        outputWorkingHours = findViewById(R.id.outputWorkingHours)
        outputBookingDate = findViewById(R.id.outputBookingDate)

        bookingDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        workingHoursSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                val selectedWorkingHours = parent?.getItemAtPosition(position).toString()
                outputWorkingHours.text = "Working Hours   : $selectedWorkingHours"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val buttonBookNanny = findViewById<Button>(R.id.buttonBookNanny)
        buttonBookNanny.setOnClickListener {
            bookNanny()
        }

        // Removed the call to disableBookedHours() from here
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
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                outputBookingDate.text = "Booking Date      : $selectedDate"
                bookingDateEditText.setText(selectedDate)
                // Disable booked hours for the selected date
                disableBookedHours(selectedDate) // Pass selectedDate here
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set batasan minimal pemilihan tanggal ke tanggal hari ini + 1
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun disableBookedHours(selectedDate: String) { // Add selectedDate parameter
        val user = auth.currentUser
        val userId = user?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("BookingDaily").whereEqualTo("userID", userId)
            .whereEqualTo("nannyID", nannyId)
            .whereEqualTo("bookDate", selectedDate) // Use selectedDate here
            .get().addOnSuccessListener { documents ->
                val bookedHours = mutableSetOf<String>()
                for (document in documents) {
                    val bookHours = document.getString("bookHours")
                    bookHours?.let {
                        bookedHours.add(it)
                    }
                }
                // Setup Spinner with booked hours disabled
                setupWorkingHoursSpinner(bookedHours)
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: ", e)
            }
    }

    private fun setupWorkingHoursSpinner(bookedHours: Set<String>) {
        val workingHoursArray = resources.getStringArray(R.array.working_hours_array)
        val adapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, workingHoursArray) {
            override fun isEnabled(position: Int): Boolean {
                return !bookedHours.contains(getItem(position))
            }

            override fun getDropDownView(
                position: Int, convertView: android.view.View?, parent: android.view.ViewGroup
            ): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                if (bookedHours.contains(getItem(position))) {
                    textView.setTextColor(resources.getColor(android.R.color.darker_gray))
                } else {
                    textView.setTextColor(resources.getColor(android.R.color.black))
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        workingHoursSpinner.adapter = adapter
    }

    private fun bookNanny() {
        val selectedDate = bookingDateEditText.text.toString()
        val selectedWorkingHours = outputWorkingHours.text.toString().substringAfter(":").trim()

        val user = auth.currentUser
        val userId = user?.uid ?: ""

        val db = FirebaseFirestore.getInstance()

        // Ensure that both date and working hours are selected
        if (selectedDate.isNotEmpty() && selectedWorkingHours.isNotEmpty()) {
            // Query untuk memeriksa apakah ada booking yang bentrok
            db.collection("BookingDaily").whereEqualTo("nannyID", nannyId)
                .whereEqualTo("bookHours", selectedWorkingHours)
                .whereEqualTo("bookDate", selectedDate).get().addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Tidak ada booking yang bentrok, tambahkan booking baru
                        val booking = hashMapOf(
                            "userID" to userId,
                            "nannyID" to nannyId,
                            "bookHours" to selectedWorkingHours,
                            "bookDate" to selectedDate,
                            "totalCost" to "Rp400.000,00/8 hours"
                        )

                        // Tambahkan data booking ke Firestore
                        db.collection("BookingDaily").add(booking)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG, "DocumentSnapshot added with ID: ${documentReference.id}"
                                )

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
                                userRef.update(
                                    "bookIDs", FieldValue.arrayUnion(documentReference.id)
                                ).addOnSuccessListener {
                                        Log.d(TAG, "bookID added to user document successfully")
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "Error updating user document with bookID", e)
                                    }

                                // Tampilkan dialog sukses booking
                                showSuccessDialog()
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                Toast.makeText(this, "Failed to book nanny", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        // Ada booking yang bentrok, tampilkan pesan error
                        Toast.makeText(
                            this,
                            "This nanny is already booked for the selected hours on the selected date.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error checking booking conflicts", e)
                    Toast.makeText(this, "Failed to check booking conflicts", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            // Show error message if date or working hours are not selected
            Toast.makeText(this, "Please select date and working hours", Toast.LENGTH_SHORT).show()
        }
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
        private const val TAG = "BookDailyActivity"
    }
}