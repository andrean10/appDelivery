package com.kontrakanprojects.appdelivery.view.admin.tracking.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailTrackingBinding
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.tracking.DetailTrackingAdapter
import com.kontrakanprojects.appdelivery.view.admin.tracking.TrackingBarangViewModel
import www.sanju.motiontoast.MotionToast

class DetailTrackingFragment : Fragment() {

    private var _binding: FragmentDetailTrackingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TrackingBarangViewModel>()
    private lateinit var detailTrackingAdapter: DetailTrackingAdapter
    private var idBarang = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        val args = DetailTrackingFragmentArgs.fromBundle(arguments as Bundle)
        idBarang = args.idBarang
        init()
    }

    private fun init(){
        with(binding){
            detailTrackingAdapter = DetailTrackingAdapter()
            with(rvWaybillDetailAdmin){
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = detailTrackingAdapter
            }
            isLoading(true)
            viewModel.listTrackingbrg(idBarang).observe(viewLifecycleOwner, {response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        detailTrackingAdapter.setData(result)
                    } else {
                        showMessage(requireActivity(),
                            getString(R.string.failed),
                            response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(), getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Detail Tracking"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}