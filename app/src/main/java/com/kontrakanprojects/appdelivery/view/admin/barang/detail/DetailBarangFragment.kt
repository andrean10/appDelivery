package com.kontrakanprojects.appdelivery.view.admin.barang.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailBarangBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragment
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragmentArgs
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
        viewModel.detalBarang(idBarang).observe(viewLifecycleOwner, { response ->
//            isLoading(false)
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

    private fun prepare(result: ResultDetailBarang?) {
        if (result != null) {
            with(binding) {
                if (result.statusBarang == "1"){
                    tvStatusPackage.setText(getString(R.string.rb_001))
                }else if (result.statusBarang == "2"){
                    tvStatusPackage.setText(getString(R.string.rb_002))
                }else if (result.statusBarang == "3"){
                    tvStatusPackage.setText(getString(R.string.rb_003))
                }else if (result.statusBarang == "4"){
                    tvStatusPackage.setText(getString(R.string.rb_004))
                }

                tvCodeCostumer.setText(Integer.toString(result.kodePelanggan!!))
                tvNameCostumer.setText(result.penerima)
                tvNumberPhone.setText(result.nomorHp)
                tvAddressCostumer.setText(result.alamat)
                courierSend.setText(Integer.toString(result.idKurir!!))
                tvDetailPackage.setText(result.detailBarang)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}