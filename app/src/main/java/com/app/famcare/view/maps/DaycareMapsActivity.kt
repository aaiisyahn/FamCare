package com.app.famcare.view.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.famcare.R
import com.app.famcare.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

class DaycareMapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DaycareAdapter
    private lateinit var tvUserLocation: TextView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "DaycareMapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        tvUserLocation = findViewById(R.id.tv_UserLocation)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        adapter = DaycareAdapter(emptyList())
        recyclerView.adapter = adapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
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

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(location.latitude, location.longitude)
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.get(0)?.getAddressLine(0) ?: "Unknown location"
                tvUserLocation.text = address
                loadDaycareData(location)
            }
        }
    }

    private fun loadDaycareData(userLocation: Location) {
        progressBar.visibility = View.VISIBLE // Menampilkan ProgressBar

        val firestore = FirebaseFirestore.getInstance()
        val daycareCollection = firestore.collection("Daycare")

        daycareCollection.get()
            .addOnSuccessListener { result ->
                val daycares = mutableListOf<Daycare>()
                for (document in result) {
                    val geoPoint = document.getGeoPoint("Coordinat")
                    val locationName = document.getString("Location")
                    val daycareName = document.getString("Name")
                    val photoURL = document.getString("PhotoURL")

                    if (geoPoint != null && locationName != null && daycareName != null && photoURL != null) {
                        val daycareLocation = Location("").apply {
                            latitude = geoPoint.latitude
                            longitude = geoPoint.longitude
                        }
                        val distanceInMeters = userLocation.distanceTo(daycareLocation)
                        val distanceInKilometers = distanceInMeters / 1000
                        val df = DecimalFormat("#.#")
                        df.roundingMode = RoundingMode.HALF_UP
                        val roundedDistanceInKilometers = df.format(distanceInKilometers).toDouble()

                        daycares.add(Daycare(daycareName, locationName, photoURL, geoPoint, roundedDistanceInKilometers))
                        Log.d(TAG, "Distance from user to $daycareName: $roundedDistanceInKilometers km")
                    } else {
                        Log.d(TAG, "Document is missing required fields")
                    }
                }

                progressBar.visibility = View.GONE // Sembunyikan ProgressBar setelah selesai loading
                daycares.sortBy { it.distanceFromUser }
                adapter.updateDaycares(daycares)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                progressBar.visibility = View.GONE // Sembunyikan ProgressBar jika terjadi kegagalan
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                tvUserLocation.text = "-"
                loadDaycareDataWithoutUserLocation()
            }
        }
    }

    private fun loadDaycareDataWithoutUserLocation() {
        progressBar.visibility = View.VISIBLE // Menampilkan ProgressBar

        val firestore = FirebaseFirestore.getInstance()
        val daycareCollection = firestore.collection("Daycare")

        daycareCollection.get()
            .addOnSuccessListener { result ->
                val daycares = mutableListOf<Daycare>()
                for (document in result) {
                    val geoPoint = document.getGeoPoint("Coordinat")
                    val locationName = document.getString("Location")
                    val daycareName = document.getString("Name")
                    val photoURL = document.getString("PhotoURL")

                    if (geoPoint != null && locationName != null && daycareName != null && photoURL != null) {
                        daycares.add(Daycare(daycareName, locationName, photoURL, geoPoint, 0.0)) // Default value for distanceFromUser is 0.0
                    } else {
                        Log.d(TAG, "Document is missing required fields")
                    }
                }
                adapter.updateDaycares(daycares)
                progressBar.visibility = View.GONE // Sembunyikan ProgressBar setelah selesai loading
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                progressBar.visibility = View.GONE // Sembunyikan ProgressBar jika terjadi kegagalan
            }
    }
}
