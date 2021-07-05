package com.kontrakanprojects.appdelivery.view.courier.barang.manage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityMapsBinding
import com.kontrakanprojects.appdelivery.db.LatLong
import com.kontrakanprojects.appdelivery.utils.showMessage
import www.sanju.motiontoast.MotionToast
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var geocoder: Geocoder

    private var myLocationMarker: Marker? = null
    private var myLocationLatLong: LatLng? = null
    private var isLocationPermissionGranted = false

    companion object {
        const val RESULT_LATLONG = "result_latlong"
        const val REQUEST_MAPS = 100
        private val DEFAULT_INDONESIA = LatLng(-2.3196972, 99.4100731)
        private const val DEFAULT_ZOOM = 5f
        private const val ZOOM_MARKER = 15f
    }

    private val TAG = MapsActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        permission()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        with(binding) {
            fabMyLocation.setOnClickListener {
                if (isLocationPermissionGranted) {
                    checkGPS()
                }
            }

            btnPickMyLocation.setOnClickListener {
                val intent = Intent().apply {
                    putExtra(RESULT_LATLONG,
                        LatLong(myLocationLatLong!!.latitude, myLocationLatLong!!.longitude))
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // options maps
        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true
        mMap.uiSettings.apply {
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        if (isLocationPermissionGranted) {
            getMyLocation()
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_INDONESIA,
                DEFAULT_ZOOM))
        }

        mMap.setOnMapClickListener { latLng ->
            myLocationMarker!!.remove()
            myLocationMarker = null
            myLocationLatLong = latLng

            val markerOptions = MarkerOptions().apply {
                position(LatLng(latLng.latitude, latLng.longitude))
                title("My Location")
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver))
            }

            myLocationMarker = mMap.addMarker(markerOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_MARKER))
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 3000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val locationSettingsResponse = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())

        geocoder = Geocoder(this, Locale.getDefault())

        locationSettingsResponse.addOnCompleteListener { task ->
            if (task.isComplete) {
                getMyLocation()
                try {
                    val response = task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        val resolvableApiException = e as ResolvableApiException
                        try {
                            resolvableApiException.startResolutionForResult(this, 101)
                        } catch (sendIntentException: IntentSender.SendIntentException) {
                            sendIntentException.printStackTrace()
                        }
                    } else if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        showMessage(this, getString(R.string.failed),
                            "Setting not available", MotionToast.TOAST_ERROR)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            myLocationMarker?.remove()
            myLocationMarker = null

            val location = task.result

            try {
                // Initialize address list
                val address = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )

                myLocationLatLong = LatLng(address.first().latitude, address.first().longitude)
                myLocationMarker =
                    mMap.addMarker(MarkerOptions()
                        .position(myLocationLatLong)
                        .title("My Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver)))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLong,
                    ZOOM_MARKER))

                // visible cardview
                binding.cvMaps.visibility = View.VISIBLE
            } catch (e: Exception) {
                showMessage(this, getString(R.string.not_found),
                    "Lokasi tidak ditemukan", MotionToast.TOAST_ERROR)
            }
        }
    }

    private fun permission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            permission.entries.forEach {
                if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                    permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                    isLocationPermissionGranted = true
                    checkGPS()
                } else {
                    isLocationPermissionGranted = false
                    showMessage(this, "Warning", "Izin lokasi dibutuhkan!",
                        MotionToast.TOAST_WARNING)
                }
            }
        }
}