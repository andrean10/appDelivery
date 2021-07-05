package com.kontrakanprojects.appdelivery.view.courier.barang.first

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.kontrakanprojects.appdelivery.view.courier.barang.BarangKurirViewModel
import com.kontrakanprojects.appdelivery.view.courier.barang.ManageTrackingKurirActivity
import com.kontrakanprojects.appdelivery.view.courier.barang.adapter.BarangKurirFirstAdapter
import www.sanju.motiontoast.MotionToast

class FirstProsesFragment : Fragment() {

    private var _binding: FragmentFirstProsesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangKurirViewModel>()
    private lateinit var barangKurirFirstAdapter: BarangKurirFirstAdapter

    private var user = 0

    private val TAG = FirstProsesFragment::class.simpleName

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
//                intent.putExtra("id_barang", idBarang)
//                intent.putExtra("status_barang", statusBarang)
                activity?.startActivity(intent)
            }
        })
    }

    private fun init(){
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
                if (response != null) {
                    Log.d(TAG, "init: ${response.results}")

                    if (response.results != null) {
                        val result = response.results
                        val a = ArrayList<ResultsBarangKurir>();
                        result.forEach {
                            if (it.statusBarang!!.toInt() < 4) {
                                a.add(it)
                            }
                        }

                        barangKurirFirstAdapter.setData(a)
                    } else {

                        textView.visibility = View.GONE
                        rvCourirListFirst.visibility = View.GONE
                        animationNotFound.visibility = View.VISIBLE
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
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