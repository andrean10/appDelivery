package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentManageBarangBinding
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel

class ManageBarangFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentManageBarangBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by viewModels<BarangViewModel>()
    private var viewModel: BarangViewModel? = null

    private var idBarang = 0
    private var request = 0
    private var latLong: HashMap<String, String>? = null
    private var valid = true

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
    }

    private val TAG = ManageBarangFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity())[BarangViewModel::class.java]
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
                init()
            }
        }

        setToolbarTitle(titleToolbar)

        with(binding) {
            btnChooseLocation.setOnClickListener(this@ManageBarangFragment)
            btnSavePackage.setOnClickListener(this@ManageBarangFragment)
        }

        // observe location maps
        viewModel!!.location.observe(viewLifecycleOwner, { results ->
            if (results != null) {
                latLong = hashMapOf()
                for (result in results) {
                    latLong!![result.key] = result.value
                }

                Log.d(TAG, "init: ${results.entries}")
            }
        })
    }

    private fun init() {
        // TODO: 14/06/2021
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_choose_location -> {
                findNavController().navigate(R.id.action_manageBarangFragment_to_mapsFragment)
            }
            R.id.btn_save_package -> { // TODO: 14/06/2021  }
            }
        }
    }

    private fun setToolbarTitle(titleToolbar: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = titleToolbar
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

}