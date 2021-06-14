package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentMapsBinding
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var viewModel: BarangViewModel? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mLastKnownLocation: Location? = null
    private lateinit var locationRequest: LocationRequest
    private var gMap: GoogleMap? = null
    private var mCurrentMarker: Marker? = null

    private var isLocationPermissionGranted = false

    companion object {
        private val DEFAULT_INDONESIA = LatLng(-2.3196972, 99.4100731)
        private const val DEFAULT_ZOOM = 3f
    }

    private val TAG = MapsFragment::class.simpleName

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_INDONESIA, DEFAULT_ZOOM))

        // enable zoom controls for the map
        googleMap.uiSettings.apply {
            isCompassEnabled = true
        }
        googleMap.isTrafficEnabled = true
        googleMap.isBuildingsEnabled = true
        googleMap.setOnMapClickListener { latLng ->
            mCurrentMarker?.remove()

            val markerOptions = MarkerOptions().position(LatLng(latLng.latitude, latLng.longitude))
//            markerOptions.title("Latitude: ${latLng.latitude}\nLongitude: ${latLng.longitude}")
            mCurrentMarker = googleMap.addMarker(markerOptions)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // set to viewmodel
            val latLong = HashMap<String, String>()
            latLong["latitude"] = latLng.latitude.toString()
            latLong["longitude"] = latLng.longitude.toString()
            viewModel?.setLocation(latLong)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity())[BarangViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permission()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.fabMyLocation.setOnClickListener {
            if (isLocationPermissionGranted) {
                checkGPS()
            }
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

        val locationSettingsResponse = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        locationSettingsResponse.addOnCompleteListener { task ->
            if (task.isComplete) {
                getMyLocation()
                try {
                    val response = task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        val resolvableApiException = e as ResolvableApiException
                        try {
                            resolvableApiException.startResolutionForResult(requireActivity(), 101)
                        } catch (sendIntentException: IntentSender.SendIntentException) {
                            sendIntentException.printStackTrace()
                        }
                    } else if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        showMessage(requireActivity(), getString(R.string.failed),
                            "Setting not available", MotionToast.TOAST_ERROR)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())

            // Initialize address list
            val addresses = geoCoder.getFromLocation(
                location.latitude, location.longitude, 1
            )
            val latLong = LatLng(addresses.first().latitude, addresses.first().longitude)
            mCurrentMarker = gMap!!.addMarker(MarkerOptions().position(latLong))
            gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f))
        }
    }

    private fun permission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            permission.entries.forEach {
                Log.d(TAG, "${it.key} = ${it.value} ")

                if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                    permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                    isLocationPermissionGranted = true
//                showMessage(requireActivity(), getString(R.string.success), "Izin diberikan",
//                    MotionToast.TOAST_SUCCESS)
                } else {
                    isLocationPermissionGranted = false
                    showMessage(requireActivity(), "Warning", "Izin lokasi dibutuhkan!",
                        MotionToast.TOAST_WARNING)
                }
            }
        }

//    private fun getDeviceLocation() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            if (isLocationPermissionGranted) {
//                val locationResult: Task<Location> = mFusedLocationProviderClient!!.lastLocation
//                locationResult.addOnCompleteListener(requireActivity()) { task ->
//                    if (task.isSuccessful) {
//                        // Set the map's camera position to the current location of the device.
//                        mLastKnownLocation = task.result
//                        Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude())
//                        Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude())
//                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                            LatLng(mLastKnownLocation.getLatitude(),
//                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM))
//                    } else {
//                        Log.d(TAG, "Current location is null. Using defaults.")
//                        Log.e(TAG, "Exception: %s", task.exception)
//                        gMap.moveCamera(CameraUpdateFactory
//                            .newLatLngZoom(DEFAULT_INDONESIA, DEFAULT_ZOOM))
//                    }
//                    getCurrentPlaceLikelihoods()
//                }
//            }
//        } catch (e: SecurityException) {
//            Log.e("Exception: %s", e.message)
//        }
//    }

//    private fun pickCurrentPlace() {
//        if (gMap == null) {
//            return
//        }
//
//        if (isLocationPermissionGranted) {
//            getDeviceLocation()
//        } else {
//            // The user has not granted permission.
//            Log.i(TAG, "The user did not grant location permission.")
//
//            // Add a default marker, because the user hasn't selected a place.
//            gMap!!.addMarker(MarkerOptions()
//                .title(getString(R.string.default_info_title))
//                .position(DEFAULT_INDONESIA)
//                .snippet(getString(R.string.default_info_snippet)))
//
//            // Prompt the user for permission.
//            permission()
//        }
//    }

    private fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): String {
        // hitung perbedaan longitude
        val longDiff = long1 - long2
        // hitung jarak
        var distance = sin(deg2rad(lat1)) *
                sin(deg2rad(lat2)) +
                cos(deg2rad(lat1)) *
                cos(deg2rad(lat2)) *
                cos(deg2rad(longDiff))
        distance = acos(distance)

        // konversi jarak radian ke degree
        distance = rad2deg(distance)
        // jarak dalam m
        distance *= 60 * 1.1515
        // jarak dalam km
        distance *= 1.609344
        // set to distance
        return String.format(Locale.US, "%2f Kilometer", distance)
    }

    private fun rad2deg(distance: Double) = (distance * 180.0 / PI)

    // konversi degree ke radian
    private fun deg2rad(lat1: Double) = (lat1 * PI / 180.0)


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}