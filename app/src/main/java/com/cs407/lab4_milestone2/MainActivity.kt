package com.cs407.lab4_milestone2

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location : Location ->
            updateLocationInfo(location)
        }

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            startListening(locationListener)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0L, 0f, locationListener)
            val location : Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location != null) {
                updateLocationInfo(location)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening(locationListener)
            }
        }
    }

    private fun startListening(locationListener: LocationListener) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        }
    }

    private fun updateLocationInfo(location: Location) {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        findViewById<TextView>(R.id.heading).text = getString(R.string.title_of_app)
        findViewById<TextView>(R.id.alt).text = getString(R.string.altitude_label) + " " + location.altitude
        findViewById<TextView>(R.id.acc).text = getString(R.string.accuracy_label) + " " + location.accuracy
        findViewById<TextView>(R.id.lon).text = getString(R.string.longitude_label) + " " + location.longitude
        findViewById<TextView>(R.id.lat).text = getString(R.string.latitude_label) + " " + location.latitude

        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        ) { addresses ->
            if(addresses.isNotEmpty()) {
                val address = addresses[0]
                var addressText = getString(R.string.address_label)
                if (address.subThoroughfare != null) addressText += "\n${address.subThoroughfare} "
                if (address.thoroughfare != null) addressText += "${address.thoroughfare}\n"
                if (address.locality != null) addressText += "${address.locality}\n"
                if (address.postalCode != null) addressText += "${address.postalCode}\n"
                if (address.countryName != null) addressText += "${address.countryName}"
                findViewById<TextView>(R.id.addr).text = addressText
            } else {
                findViewById<TextView>(R.id.addr).text = getString(R.string.address_label) + getString(R.string.not_found_text)
            }
        }
    }
}