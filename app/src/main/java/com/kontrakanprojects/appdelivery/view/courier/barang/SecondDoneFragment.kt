package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentFirstProsesBinding
import com.kontrakanprojects.appdelivery.databinding.FragmentSecondDoneBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import www.sanju.motiontoast.MotionToast

class SecondDoneFragment : Fragment() {

    private var _binding: FragmentSecondDoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangKurirViewModel>()
    private lateinit var barangKurirSecondAdapter: BarangKurirSecondAdapter

    private var user = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecondDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserPreference(requireContext()).getUser().idUser!!
        init()
    }

    private fun init(){
        with(binding){
            barangKurirSecondAdapter = BarangKurirSecondAdapter(requireActivity())
            with(rvCourirListFirst){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = barangKurirSecondAdapter
            }
            isLoading(true)
            viewModel.detailKurir(user).observe(viewLifecycleOwner, {response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        var a = ArrayList<ResultsBarangKurir>();
                        result?.forEach {
                            if (it.statusBarang!!.toInt() == 4){
                                a.add(it)
                            }
                        }
                        barangKurirSecondAdapter.setData(a)
                    }else{
                        showMessage(requireActivity(),
                            "Failed",
                            response.message,
                            MotionToast.TOAST_ERROR)
                    }
                }else { // failed mengambil data
                    showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
                }
            })
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}