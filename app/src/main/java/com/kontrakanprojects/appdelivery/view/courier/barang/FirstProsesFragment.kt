package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentFirstProsesBinding
import com.kontrakanprojects.appdelivery.databinding.FragmentListCouriersBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.CouriersViewModel
import com.kontrakanprojects.appdelivery.view.admin.couriers.ListCouriersAdapter
import www.sanju.motiontoast.MotionToast

class FirstProsesFragment : Fragment() {

    private var _binding: FragmentFirstProsesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangKurirViewModel>()
    private lateinit var barangKurirFirstAdapter: BarangKurirFirstAdapter

    private var user = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstProsesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserPreference(requireContext()).getUser().idUser!!
        init()

        barangKurirFirstAdapter.setOnItemClickCallBack(object : BarangKurirFirstAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultsBarangKurir: ResultsBarangKurir) {
                val toManageTracking =
                    FirstProsesFragmentDirections.actionFirstProsesFragmentToManageFragment()
                findNavController().navigate(toManageTracking)
            }
        })
    }

    private fun init(){
        Log.d("asdasd", "init: dijalankan")
        with(binding){
            barangKurirFirstAdapter = BarangKurirFirstAdapter(requireActivity())
            with(rvCourirListFirst){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = barangKurirFirstAdapter
            }
            isLoading(true)
            viewModel.detailKurir(user).observe(viewLifecycleOwner, { response ->
                isLoading(false)
                Log.d("resppo", "init: responbalek")
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        var a = ArrayList<ResultsBarangKurir>();
                        result?.forEach {
                            if (it.statusBarang!!.toInt() < 4) {
                                a.add(it)
                            }
                        }
                        barangKurirFirstAdapter.setData(a)
                    } else {
                        showMessage(requireActivity(),
                            "Failed",
                            response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    companion object {
        fun newInstance(): FirstProsesFragment {
            return FirstProsesFragment()
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("resppo", "onDestroy: ")
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.d("resppo", "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d("resppo", "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d("resppo", "onStop: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d("resppo", "onStart: ")
    }
}