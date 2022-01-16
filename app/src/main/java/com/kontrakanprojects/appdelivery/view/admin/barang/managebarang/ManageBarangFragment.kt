package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentManageBarangBinding
import com.kontrakanprojects.appdelivery.db.LatLong
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import www.sanju.motiontoast.MotionToast

class ManageBarangFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentManageBarangBinding? = null
    private val binding get() = _binding!!
    private var viewModel: BarangViewModel? = null

    private var resultBarang: ResultDetailBarang? = null
    private var myLocationLat = ""
    private var myLocationLong = ""
    private var destinationLat = ""
    private var destinationLong = ""
    private var distance = ""
    private var statusBarang = ""
    private var statusEstiminasi = ""

    private var idBarang = 0
    private var request = 0
    private var idKurirFromSpinner = 0
    private var valid = true
    private var isValid = true

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    private val TAG = ManageBarangFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity())[BarangViewModel::class.java]
        setHasOptionsMenu(true)

        val cb = activity?.onBackPressedDispatcher?.addCallback(this) {
            viewModel = null

            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManageBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLocation()

        val args = ManageBarangFragmentArgs.fromBundle(arguments as Bundle)
        idBarang = args.idBarang
        request = args.idRequest

        lateinit var titleToolbar: String
        when (request) {
            REQUEST_ADD -> {
                titleToolbar = "Tambah Data Barang"
            }
            REQUEST_EDIT -> {
                titleToolbar = "Edit Data Barang"
                observeEdit(idBarang)
            }
        }

        setToolbarTitle(titleToolbar)
        prepareSpinnerItem()

        with(binding) {
            btnChooseLocation.setOnClickListener(this@ManageBarangFragment)
            btnSavePackage.setOnClickListener(this@ManageBarangFragment)
        }
    }

    private fun observeLocation() {
        // observe location maps
        viewModel!!.location.observe(viewLifecycleOwner, { results ->
            if (results != null) {
                // init lat long and distance
                val myLocation = results["location"] as LatLong
                myLocationLat = myLocation.latitude.toString()
                myLocationLong = myLocation.longitude.toString()

                val myDestination = results["destination"] as LatLong
                destinationLat = myDestination.latitude.toString()
                destinationLong = myDestination.longitude.toString()
                distance = results["distance"] as String

                binding.tvLocationLatlong.text =
                    getString(R.string.latlong, destinationLat, destinationLong)

                Log.d(TAG, "onViewCreated: ${results.entries}")
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_choose_location -> {
                when (request) {
                    REQUEST_ADD -> {
                        findNavController().navigate(R.id.action_manageBarangFragment_to_mapsFragment)
                    }
                    REQUEST_EDIT -> {
                        val toMaps =
                            ManageBarangFragmentDirections.actionManageBarangFragmentToMapsFragment()
                        toMaps.idRequest = MapsFragment.REQUEST_EDIT
                        toMaps.latLong =
                            LatLong(destinationLat.toDouble(), destinationLong.toDouble())
                        toMaps.distance = distance
                        findNavController().navigate(toMaps)
                    }
                }
            }
            R.id.btn_save_package -> {
                if (request == REQUEST_ADD) {
                    checkField(true)
                } else {
                    checkField()
                }
            }
        }
    }

    private fun prepareSpinnerItem() {
        viewModel!!.listKurir().observe(viewLifecycleOwner, { response ->
            if (response != null) {
                // add data semua nama program ke list
                if (response.results != null) {
                    setAdapterSpinner(response.results)
                }
            }
        })
    }

    private fun checkField(isAdd: Boolean = false) {
        with(binding) {
            when{
                rbOneDay.isChecked -> {
                    statusEstiminasi = "1"
                }
                rbTwoDay.isChecked -> {
                    statusEstiminasi = "2"
                }
                rbThreeDay.isChecked -> {
                    statusEstiminasi = "3"
                }
                rbFourDay.isChecked -> {
                    statusEstiminasi = "4"
                }
                rbFifthDay.isChecked -> {
                    statusEstiminasi = "5"
                }
                else -> {
                    isValid = false
                }
            }
            val kodePelanggan = etCostumerCode.text.toString().trim()
            val penerima = etCostumerName.text.toString().trim()
            val noHp = etPhoneNumber.text.toString().trim()
            val alamatLengkap = etFullAddress.text.toString().trim()
            val detailBarang = etPackageDetail.text.toString().trim()

            // check barang
            checkValue(kodePelanggan, etCostumerCode)
            checkValue(penerima, etCostumerName)
            checkValue(noHp, etPhoneNumber)
            checkValue(alamatLengkap, etFullAddress)
            checkValue(detailBarang, etPackageDetail)

            if (isAdd) { // pengecekkan value lokasi
                checkValueLocation(myLocationLat)
                checkValueLocation(myLocationLong)
                checkValueLocation(destinationLat)
                checkValueLocation(destinationLong)
                checkValueLocation(distance)
            } else {
                checkValueLocation(destinationLat)
                checkValueLocation(destinationLong)
                checkValueLocation(distance)
            }

            if (idKurirFromSpinner == 0) { // check value idkurir
                spinnerKurir.errorText = NOT_NULL
                valid = false
                return@with
            }

            val params = hashMapOf(
                "kode_pelanggan" to kodePelanggan,
                "penerima" to penerima,
                "nomor_hp" to noHp,
                "alamat" to alamatLengkap,
                "latitude" to destinationLat,
                "longitude" to destinationLong,
                "distance" to distance,
                "id_kurir" to idKurirFromSpinner.toString(),
                "detail_barang" to detailBarang,
                "estiminasi" to statusEstiminasi
            )

            if (valid) {
                if (isAdd) {
                    params["latitude_tracking"] = myLocationLat
                    params["longitude_tracking"] = myLocationLong
                    Log.d("agaa", "checkField: $params")
                    addBarang(params)
                } else {
                    params["status_barang"] = statusBarang
                    editBarang(idBarang, params)
                }
            }
        }
    }

    private fun observeEdit(idBarang: Int) {
        viewModel!!.detailBarang(idBarang).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results?.get(0)
                    if (result != null) {
                        resultBarang = result
                        prepareEdit(result)
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepareEdit(result: ResultDetailBarang) {
        with(binding) {
            etCostumerCode.setText(result.kodePelanggan.toString())
            etCostumerName.setText(result.penerima)
            etPhoneNumber.setText(result.nomorHp)
            etFullAddress.setText(result.alamat)
            etPackageDetail.setText(result.detailBarang)

            statusEstiminasi = result.estiminasi.toString()

            when{
                (statusEstiminasi == "1") -> {
                    rbOneDay.isChecked = true
                }
                (statusEstiminasi == "2") -> {
                    rbTwoDay.isChecked = true
                }
                (statusEstiminasi == "3") -> {
                    rbThreeDay.isChecked = true
                }
                (statusEstiminasi == "4") -> {
                    rbFourDay.isChecked = true
                }
                (statusEstiminasi == "5") -> {
                    rbFifthDay.isChecked = true
                }
            }


            // passing data lokasi dan jarak ke textview dan variabel
            tvLocationLatlong.text = getString(R.string.latlong, result.latitude, result.longitude)
            destinationLat = result.latitude.toString()
            destinationLong = result.longitude.toString()
            distance = result.distance.toString()
            statusBarang = result.statusBarang.toString()
        }
    }

    private fun setAdapterSpinner(results: List<ResultKurir>) {
        with(binding) {
            // set to spinneradapter
            spinnerKurir.item = results
            // set spinneritem pada saat update data
            if (request == REQUEST_EDIT) {
                var idSelectedKurir = 0
                results.forEachIndexed { i, resultKurir ->
                    if (resultKurir.idKurir == resultBarang?.idKurir) {
                        idSelectedKurir = i
                    }
                }

//                idKurirFromSpinner = idSelectedKurir

//              set kurir yang terdata di kegiatan dan jadikan nilai default awal di spinner
                spinnerKurir.setSelection(idSelectedKurir)
            }

            // mengambil data id kurir pada saat dipilih
            spinnerKurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    // get selected item spinner and casting using ResultKurir
                    val itemKurir = parent?.selectedItem as ResultKurir
                    // passed data and get id program
                    idKurirFromSpinner = itemKurir.idKurir!!

                    if (idKurirFromSpinner > 0) {
                        spinnerKurir.errorText = null
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun addBarang(params: HashMap<String, String>) {
        isLoading(true)
        viewModel!!.addbarang(params).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    findNavController().navigateUp()
                    showMessage(requireActivity(), getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun editBarang(idBarang: Int, params: HashMap<String, String>) {
        isLoading(true)
        viewModel!!.editBarang(idBarang, params).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    findNavController().navigateUp()
                    showMessage(requireActivity(), getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun deleteBarang(idBarang: Int) {
        isLoading(true)
        viewModel!!.deleteBarang(idBarang).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    findNavController().navigateUp()
                    showMessage(requireActivity(), getString(R.string.success), response.message,
                        MotionToast.TOAST_SUCCESS)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed), response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun checkValue(value: String?, editText: TextInputEditText) {
        if (value.isNullOrEmpty()) {
            editText.error = NOT_NULL
            valid = false
            return
        }
    }

    private fun checkValueLocation(value: String?) {
        if (value.isNullOrEmpty()) {
            with(binding) {
                tvFailedLocation.visibility = View.VISIBLE
                tvLocationLatlong.visibility = View.GONE
            }
            valid = false
            return
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                progressBar.visibility = View.VISIBLE
                btnSavePackage.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                btnSavePackage.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (request == REQUEST_EDIT) {
            inflater.inflate(R.menu.delete, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                viewModel = null
                findNavController().navigateUp()
            }
            R.id.delete -> deleteBarang(idBarang)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle(titleToolbar: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = titleToolbar
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}