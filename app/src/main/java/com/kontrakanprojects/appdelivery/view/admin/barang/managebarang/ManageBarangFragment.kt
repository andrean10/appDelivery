package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentManageBarangBinding
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel

// Class untuk CRUD Barang
class ManageBarangFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentManageBarangBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangViewModel>()

    private var idBarang = 0
    private var request = 0
    private var valid = true

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
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

        with(binding) {}
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
//                val intent =
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
//            val place = getP
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