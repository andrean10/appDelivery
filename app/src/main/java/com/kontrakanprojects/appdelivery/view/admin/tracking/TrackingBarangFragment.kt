package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.databinding.FragmentTrackingBarangBinding
import com.kontrakanprojects.appdelivery.model.tracking.ResultsItem
import com.kontrakanprojects.appdelivery.utils.showMessage
import www.sanju.motiontoast.MotionToast

class TrackingBarangFragment : Fragment() {

    private var _binding: FragmentTrackingBarangBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TrackingBarangViewModel>()
    private lateinit var trackingBarangAdapter: TrackingBarangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle()
        init()

        trackingBarangAdapter.setOnItemClickCallBack(object : TrackingBarangAdapter.OnItemClickCallBack{
            override fun onItemClicked(resultsItem: ResultsItem) {
                val toDetailTracking = TrackingBarangFragmentDirections.
                actionTrackingBarangFragmentToDetailTrackingFragment().apply {
                    idBarang = resultsItem.idBarang ?: 0
                }
                if (toDetailTracking.idBarang != 0) findNavController().navigate(toDetailTracking)
            }
        })
    }

    private fun init() {
        with(binding) {
            trackingBarangAdapter = TrackingBarangAdapter(requireActivity())
            with(rvAllTracking) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = trackingBarangAdapter
            }

            isLoading(true)
            viewModel.listTracking().observe(viewLifecycleOwner, { response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        trackingBarangAdapter.setData(result)
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

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Tracking Barang"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}