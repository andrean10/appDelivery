package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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
import com.kontrakanprojects.appdelivery.databinding.FragmentMapsBinding
import com.kontrakanprojects.appdelivery.db.LatLong
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var viewModel: BarangViewModel? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var gMap: GoogleMap? = null
    private var myLocationMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var myLocationLatLong: LatLng? = null
    private var myDestinationLatLong: LatLng? = null
    private lateinit var geocoder: Geocoder

    private var idRequest = 0
    private var distance: String = ""
    private var isLocationPermissionGranted = false

    companion object {
        const val REQUEST_EDIT = 200
        private val DEFAULT_INDONESIA = LatLng(-2.3196972, 99.4100731)
        private const val DEFAULT_ZOOM = 5f
        private const val ZOOM_MARKER = 15f
    }

    private val TAG = MapsFragment::class.simpleName

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        googleMap.isTrafficEnabled = true
        googleMap.isBuildingsEnabled = true
        googleMap.uiSettings.apply {
            isCompassEnabled = true
            isMapToolbarEnabled = false
        }

        if (isLocationPermissionGranted) {
            if (idRequest == REQUEST_EDIT) {
                val markerOptions = MarkerOptions().apply {
                    position(LatLng(myDestinationLatLong!!.latitude,
                        myDestinationLatLong!!.longitude))
                    title("My Destination")
                }

                destinationMarker = googleMap.addMarker(markerOptions)

                with(binding) {
                    cvMaps.visibility = View.VISIBLE
                    tvDistanceLocation.text = getString(R.string.km, distance)
                }
            } else { // default jika request tambah
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_INDONESIA,
                    DEFAULT_ZOOM))
            }
        }

        googleMap.setOnMapClickListener { latLng ->
            myDestinationLatLong = latLng
            destinationMarker?.remove()

            val markerOptions = MarkerOptions().apply {
                position(LatLng(latLng.latitude, latLng.longitude))
                title("My Destination")
            }
            destinationMarker = googleMap.addMarker(markerOptions)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_MARKER))

            // init distance
            distance = distance(
                myLocationLatLong!!.latitude,
                myLocationLatLong!!.longitude,
                myDestinationLatLong!!.latitude,
                myDestinationLatLong!!.longitude,
            )

            with(binding) {
                cvMaps.visibility = View.VISIBLE
                tvDistanceLocation.text = getString(R.string.km, distance)
            }
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

        val args = MapsFragmentArgs.fromBundle(arguments as Bundle)
        idRequest = args.idRequest

        if (idRequest == REQUEST_EDIT) {
            myDestinationLatLong = LatLng(args.latLong?.latitude!!, args.latLong?.longitude!!)
            distance = args.distance
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        with(binding) {
            fabMyLocation.setOnClickListener {
                if (isLocationPermissionGranted) {
                    checkGPS()
                }
            }

            searchLocation.setOnQueryTextListener(searchMaps)

            btnPickRouteLocation.setOnClickListener {
                if (myLocationLatLong != null && myDestinationLatLong != null) { // check null latlong
                    // set to viewmodel
                    val location = HashMap<String, Any>()
                    location["location"] =
                        LatLong(myLocationLatLong!!.latitude, myLocationLatLong!!.longitude)
                    location["destination"] =
                        LatLong(myDestinationLatLong!!.latitude, myDestinationLatLong!!.longitude)
                    location["distance"] = distance
                    viewModel?.setLocation(location)

                    findNavController().navigateUp()
                }
            }
        }
    }

    private val searchMaps = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            val address = geocoder.getFromLocationName(query, 1)

            try {
                myDestinationLatLong = LatLng(address.first().latitude, address.first().longitude)
                destinationMarker =
                    gMap!!.addMarker(MarkerOptions()
                        .position(myDestinationLatLong)
                        .title("My Destination"))
                gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myDestinationLatLong,
                    ZOOM_MARKER))
            } catch (e: Exception) {
                showMessage(requireActivity(), getString(R.string.not_found),
                    "Lokasi tidak ditemukan", MotionToast.TOAST_ERROR)
            }
            return false
        }

        override fun onQueryTextChange(newText: String?) = false

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
            myLocationMarker = null
            val location = task.result

            try {
                // Initialize address list
                val address = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                )

                myLocationLatLong = LatLng(address.first().latitude, address.first().longitude)
                myLocationMarker =
                    gMap!!.addMarker(MarkerOptions()
                        .position(myLocationLatLong)
                        .title("My Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver)))
                gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLong,
                    ZOOM_MARKER))
            } catch (e: Exception) {
                showMessage(requireActivity(), getString(R.string.not_found),
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
                Log.d(TAG, "${it.key} = ${it.value} ")

                if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                    permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                    isLocationPermissionGranted = true
                    checkGPS()
                } else {
                    isLocationPermissionGranted = false
                    showMessage(requireActivity(), "Warning", "Izin lokasi dibutuhkan!",
                        MotionToast.TOAST_WARNING)
                }
            }
        }

    private fun distance(
        latLocation: Double,
        longLocation: Double,
        latDestination: Double,
        longDestination: Double,
    ): String {
        // hitung perbedaan longitude
        val longDiff = longLocation - longDestination
        // hitung jarak
        var distance = sin(deg2rad(latLocation)) *
                sin(deg2rad(latDestination)) +
                cos(deg2rad(latLocation)) *
                cos(deg2rad(latDestination)) *
                cos(deg2rad(longDiff))
        distance = acos(distance)

        // konversi jarak radian ke degree
        distance = rad2deg(distance)
        // jarak dalam m
        distance *= 60 * 1.1515
        // jarak dalam km
        distance *= 1.609344
        // set to distance
        return String.format(Locale.US, "%.2f", distance)
    }

    private fun rad2deg(distance: Double) = (distance * 180.0 / PI)

    // konversi degree ke radian
    private fun deg2rad(lat1: Double) = (lat1 * PI / 180.0)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}