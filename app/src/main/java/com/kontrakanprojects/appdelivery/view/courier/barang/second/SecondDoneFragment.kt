package com.kontrakanprojects.appdelivery.view.courier.barang.second

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentSecondDoneBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.courier.barang.adapter.BarangKurirSecondAdapter
import com.kontrakanprojects.appdelivery.view.courier.barang.detail.DetailTrackingKurirActivity
import com.kontrakanprojects.appdelivery.view.courier.viewmodel.BarangKurirViewModel
import www.sanju.motiontoast.MotionToast

class SecondDoneFragment : Fragment() {

    private var _binding: FragmentSecondDoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangKurirViewModel>()
    private lateinit var barangKurirSecondAdapter: BarangKurirSecondAdapter

    private var user = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSecondDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserPreference(requireContext()).getUser().idUser!!
        init()
        observe()

        barangKurirSecondAdapter.setOnItemClickCallBack(
            object : BarangKurirSecondAdapter.OnItemClickCallBack{
                override fun onItemClicked(resultsBarangKurir: ResultsBarangKurir) {
                    val intent = Intent(requireContext(), DetailTrackingKurirActivity::class.java)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_ID_BARANG, resultsBarangKurir.idBarang)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_KODE_PELANGGAN, resultsBarangKurir.kodePelanggan?.toInt())
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_NAMA_PENERIMA, resultsBarangKurir.penerima)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_NOMOR_HP, resultsBarangKurir.nomorHp)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_ALAMAT, resultsBarangKurir.alamat)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_STATUS_BARANG, resultsBarangKurir.statusBarang)
                    intent.putExtra(DetailTrackingKurirActivity.EXTRA_FOTO_DITERIMA, resultsBarangKurir.foto_diterima)
                    activity?.startActivity(intent)
                }
            }
        )
    }

    private fun init() {
        with(binding) {
            barangKurirSecondAdapter = BarangKurirSecondAdapter(requireActivity())
            with(rvCourirListTwo) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = barangKurirSecondAdapter
            }
        }
    }

    private fun observe() {
        isLoading(true)
        viewModel.detailKurir(user).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.results != null) {
                    val result = response.results
                    val a = ArrayList<ResultsBarangKurir>();
                    result.forEach {
                        if (it.statusBarang!!.toInt() == 6) {
                            a.add(it)
                        }
                    }

                    barangKurirSecondAdapter.setData(a)
                } else {
                    with(binding) {
                        layoutNotFound.visibility = View.VISIBLE
                        rvCourirListTwo.visibility = View.GONE
                    }
                }
            } else { // failed mengambil data
                showMessage(requireActivity(), getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    companion object {
        fun newInstance(): SecondDoneFragment {
            return SecondDoneFragment()
        }
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

    override fun onStart() {
        super.onStart()
        barangKurirSecondAdapter.clearData()
        observe()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}