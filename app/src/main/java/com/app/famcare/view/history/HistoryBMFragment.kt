package com.app.famcare.view.historyimport

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.famcare.R
import com.app.famcare.adapter.BookingAdapter
import com.app.famcare.model.BookingMonthly
import com.app.famcare.model.BookingType
import com.app.famcare.view.chat.ChatActivity
import com.app.famcare.view.detailhistory.DetailHistoryBDActivity
import com.app.famcare.view.facilities.FacilitiesActivity
import com.app.famcare.view.main.MainActivity
import com.app.famcare.view.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryBMFragment : Fragment(), BookingAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookingAdapter
    private lateinit var emptyTextView: TextView
    private var selectedBookingID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history_b_m, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerViewHistoryBM)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with empty list and this as the listener
        adapter = BookingAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        emptyTextView = rootView.findViewById(R.id.emptyTextView)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call fetchDataFromFirestore after view is created
        fetchDataFromFirestore()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchDataFromFirestore() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val db = FirebaseFirestore.getInstance()

        // Get the user document to retrieve bookIDs for monthly type
        db.collection("User").document(currentUserID).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val bookIDs = document.get("bookIDs") as? List<String> ?: emptyList()
                if (bookIDs.isNotEmpty()) {
                    fetchMonthlyBookings(bookIDs)
                } else {
                    // Show empty message when no bookings found
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    Log.w(TAG, "No bookings found for user")
                }
            } else {
                Log.w(TAG, "User document does not exist")
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting user document: ", exception)
        }
    }

    private fun fetchMonthlyBookings(bookIDs: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val bookingList = mutableListOf<BookingMonthly>()

        // Fetch each booking by bookID for monthly type
        bookIDs.forEach { bookID ->
            db.collection("BookingMonthly").document(bookID).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nannyID = document.getString("nannyID") ?: ""
                        val bookStartDate = document.getString("startDate") ?: ""
                        val bookEndDate = document.getString("endDate") ?: ""
                        db.collection("Nanny").document(nannyID).get()
                            .addOnSuccessListener { nannyDoc ->
                                val nannyName = nannyDoc.getString("name") ?: ""
                                val bookingMonthly = BookingMonthly(
                                    bookID,
                                    nannyName,
                                    bookStartDate,
                                    bookEndDate,
                                    BookingType.MONTHLY
                                )
                                bookingList.add(bookingMonthly)

                                // Reverse the list to display in the required order
                                adapter.setData(bookingList.reversed())

                                // Hide empty text message
                                emptyTextView.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Error getting nanny document", e)
                            }
                    } else {
                        // Handle case where document does not exist (optional)
                    }
                }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting booking document: ", e)
            }
        }

        // Check if bookingList is empty after fetching
        if (bookingList.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun onItemClick(bookingID: String) {
        selectedBookingID = bookingID
        val intent = Intent(requireContext(), DetailHistoryBDActivity::class.java)
        intent.putExtra("bookingID", selectedBookingID)
        startActivity(intent)
    }

    override fun onChatClick(bookingID: String) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("bookingID", bookingID)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "HistoryBMFragment"
    }
}