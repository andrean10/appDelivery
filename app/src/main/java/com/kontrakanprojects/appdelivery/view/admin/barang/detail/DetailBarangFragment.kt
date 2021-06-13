package com.kontrakanprojects.appdelivery.view.admin.barang.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailBarangBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import www.sanju.motiontoast.MotionToast

class DetailBarangFragment : Fragment() {

    private var _binding: FragmentDetailBarangBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangViewModel>()
    private var idBarang = 0

    private val TAG = DetailBarangFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idBarang = DetailBarangFragmentArgs.fromBundle(arguments as Bundle).idBarang
        init()
    }


    private fun init() {
        isLoading(true)
        viewModel.detalBarang(idBarang).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    prepare(result?.get(0))
                } else {
                    showMessage(requireActivity(),
                        "Not Found",
                        response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else { // failed mengambil data
                showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun prepare(result: ResultDetailBarang?) {
        if (result != null) {
            with(binding) {
                when (result.statusBarang) {
                    "1" -> {
                        tvStatusPackage.text = getString(R.string.rb_001)
                    }
                    "2" -> {
                        tvStatusPackage.text = getString(R.string.rb_002)
                    }
                    "3" -> {
                        tvStatusPackage.text = getString(R.string.rb_003)
                    }
                    "4" -> {
                        tvStatusPackage.text = getString(R.string.rb_004)
                    }
                }

                tvCodeCostumer.text = result.kodePelanggan.toString()
                tvNameCostumer.text = result.penerima
                tvNumberPhone.text = result.nomorHp
                tvAddressCostumer.text = result.alamat
                courierSend.text = result.idKurir.toString()
                tvDetailPackage.text = result.detailBarang
            }
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}