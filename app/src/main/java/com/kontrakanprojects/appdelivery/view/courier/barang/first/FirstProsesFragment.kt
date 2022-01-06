package com.kontrakanprojects.appdelivery.view.courier.barang.first

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentFirstProsesBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.courier.barang.adapter.BarangKurirFirstAdapter
import com.kontrakanprojects.appdelivery.view.courier.barang.manage.ManageTrackingKurirActivity
import com.kontrakanprojects.appdelivery.view.courier.viewmodel.BarangKurirViewModel
import www.sanju.motiontoast.MotionToast

class FirstProsesFragment : Fragment() {

    private var _binding: FragmentFirstProsesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangKurirViewModel>()
    private lateinit var barangKurirFirstAdapter: BarangKurirFirstAdapter

    private var user = 0

    companion object {
        fun newInstance(): FirstProsesFragment {
            return FirstProsesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFirstProsesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserPreference(requireContext()).getUser().idUser!!
        init()

        barangKurirFirstAdapter.setOnItemClickCallBack(object :
            BarangKurirFirstAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsBarangKurir: ResultsBarangKurir) {
                val intent = Intent(requireContext(), ManageTrackingKurirActivity::class.java)
                intent.putExtra(ManageTrackingKurirActivity.EXTRA_ID_BARANG,
                    resultsBarangKurir.idBarang)
                intent.putExtra(ManageTrackingKurirActivity.EXTRA_STATUS_BARANG,
                    resultsBarangKurir.statusBarang?.toInt())
                activity?.startActivity(intent)
            }
        })
    }

    private fun init() {
        with(binding) {
            barangKurirFirstAdapter = BarangKurirFirstAdapter(requireActivity())
            with(rvCourirListFirst) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = barangKurirFirstAdapter
            }

            observe()
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
                        if (it.statusBarang!!.toInt() < 6) {
                            a.add(it)
                        }
                    }

                    barangKurirFirstAdapter.setData(a)
                } else {
                    with(binding) {
                        textView.visibility = View.GONE
                        rvCourirListFirst.visibility = View.GONE
                        animationNotFound.visibility = View.VISIBLE
                    }
                }
            } else { // failed mengambil data
                showMessage(requireActivity(),
                    getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
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

    override fun onStart() {
        super.onStart()
        barangKurirFirstAdapter.clearData()
        observe()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
