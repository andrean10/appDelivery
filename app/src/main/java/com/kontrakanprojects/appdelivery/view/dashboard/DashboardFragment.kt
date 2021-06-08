package com.kontrakanprojects.appdelivery.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), View.OnClickListener {


    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnCourier.setOnClickListener(this@DashboardFragment)
            btnTracking.setOnClickListener(this@DashboardFragment)
            btnBarang.setOnClickListener(this@DashboardFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_courier -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_listCouriersFragment)
            }
            R.id.btn_tracking -> {
                // TODO: 06/06/2021
                findNavController().navigate(R.id.action_dashboardFragment_to_trackingBarangFragment)
            }
            R.id.btn_barang -> {
                // TODO: 06/06/2021
                findNavController().navigate(R.id.action_dashboardFragment_to_barangFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}