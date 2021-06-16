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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private var myDirectionLatLong: LatLng? = null
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
        val args = MapsFragmentArgs.fromBundle(arguments as Bundle)
        val idRequest = args.idRequest
        val latLong = args.latLong

        Log.d(TAG, "$idRequest: ")
        Log.d(TAG, "$latLong: ")

        gMap = googleMap
        if (!isLocationPermissionGranted) {
            if (idRequest == REQUEST_EDIT) {
                val markerOptions = MarkerOptions().apply {
                    position(LatLng(latLong!!.latitude, latLong.longitude))
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

        // enable zoom controls for the map
        googleMap.uiSettings.apply {
            isCompassEnabled = true
            isMapToolbarEnabled = false
        }
        googleMap.isTrafficEnabled = true
        googleMap.isBuildingsEnabled = true
//        googleMap.isMyLocationEnabled = true
//        googleMap.setOnMyLocationButtonClickListener {
//            Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
//                .show()
//            // Return false so that we don't consume the event and the default behavior still occurs
//            // (the camera animates to the user's current position).
//            false
//        }
//        googleMap.setOnMyLocationClickListener { location ->
//            Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG)
//                .show()
//        }

        googleMap.setOnMapClickListener { latLng ->
            myDirectionLatLong = latLng
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
                myDirectionLatLong!!.latitude,
                myDirectionLatLong!!.longitude,
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        with(binding) {
            fabMyLocation.setOnClickListener {
                if (isLocationPermissionGranted) {
                    checkGPS()
                }
            }

            btnPickRouteLocation.setOnClickListener {
                if (myLocationLatLong != null && myDirectionLatLong != null) { // check null latlong
                    // set to viewmodel
                    val location = HashMap<String, Any>()
                    location["location"] =
                        LatLong(myLocationLatLong!!.latitude, myLocationLatLong!!.longitude)
                    location["destination"] =
                        LatLong(myDirectionLatLong!!.latitude, myDirectionLatLong!!.longitude)
                    location["distance"] = distance
                    viewModel?.setLocation(location)

                    // kembali ke halaman manage fragment
                    findNavController().navigateUp()

//                showBottomSheet(distance)
                }
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

            myLocationLatLong = LatLng(addresses.first().latitude, addresses.first().longitude)
            myLocationMarker =
                gMap!!.addMarker(MarkerOptions().position(myLocationLatLong).title("My Location"))
            gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocationLatLong, ZOOM_MARKER))
        }
    }

    private fun showBottomSheet(distance: String) {
        // init bottomsheet distance
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(requireContext()).inflate(
            R.layout.bottom_sheet_distance, activity?.findViewById(R.id.bottomSheetContainer)
        )

        // set to textview
        val tvDistance = bottomSheetView.findViewById<TextView>(R.id.tv_distance_location)
        tvDistance.text = distance

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
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
        return String.format(Locale.US, "%.1f", distance)
    }

    private fun rad2deg(distance: Double) = (distance * 180.0 / PI)

    // konversi degree ke radian
    private fun deg2rad(lat1: Double) = (lat1 * PI / 180.0)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}