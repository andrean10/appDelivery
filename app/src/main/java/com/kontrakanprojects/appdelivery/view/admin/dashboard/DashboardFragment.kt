package com.kontrakanprojects.appdelivery.view.admin.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDashboardBinding
import com.kontrakanprojects.appdelivery.db.User
import com.kontrakanprojects.appdelivery.model.profile.ResultsItem
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragment
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import com.kontrakanprojects.appdelivery.view.home.HomeActivity
import www.sanju.motiontoast.MotionToast

class DashboardFragment : Fragment(), View.OnClickListener {


    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DashboardViewModel>()

    private lateinit var resultDetail: ResultsItem
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDetailProfile()

        with(binding) {
            if (user.idRole == ChooseLoginFragment.ROLE_COURIER) {
                btnTracking.visibility = View.GONE
                btnCourier.visibility = View.GONE
            }

            imgProfile.setOnClickListener(this@DashboardFragment)
            btnCourier.setOnClickListener(this@DashboardFragment)
            btnTracking.setOnClickListener(this@DashboardFragment)
            btnBarang.setOnClickListener(this@DashboardFragment)
            btnLogout.setOnClickListener(this@DashboardFragment)
        }
    }

    private fun observeDetailProfile() {
        user = UserPreference(requireContext()).getUser()

        viewModel.profile(user.idUser!!, user.idRole!!).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results?.get(0)
                    if (result != null) prepare(result)
                } else {
                    showMessage(requireActivity(), getString(R.string.failed),
                        response.message!!, MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(),
                    getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepare(result: ResultsItem) {
        resultDetail = result
        with(binding) {
            Glide.with(requireContext())
                .load(ApiConfig.URL + result.fotoProfil)
                .placeholder(R.drawable.no_profile_images)
                .error(R.drawable.no_profile_images)
                .into(imgProfile)

            tvNameWelcoming.text = getString(R.string.home_admin, result.namaLengkap)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_profile -> {
                when (user.idRole) {
                    ChooseLoginFragment.ROLE_ADMIN -> {
                        val toDetailAdmin =
                            DashboardFragmentDirections.actionDashboardFragmentToAdminProfileFragment()
                        toDetailAdmin.idAdmin = user.idUser ?: 0
                        findNavController().navigate(toDetailAdmin)
                    }
                    ChooseLoginFragment.ROLE_COURIER -> {
                        val toDetailCourier =
                            DashboardFragmentDirections.actionDashboardFragmentToDetailCouriersFragment()
                        toDetailCourier.idKurir = user.idUser ?: 0
                        toDetailCourier.idRequest = DetailCouriersFragment.REQUEST_EDIT
                        findNavController().navigate(toDetailCourier)
                    }
                }
            }
            R.id.btn_courier -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_listCouriersFragment)
            }
            R.id.btn_tracking -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_trackingBarangFragment)
            }
            R.id.btn_barang -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_barangFragment)
            }
            R.id.btn_logout -> {
                // delete preferences
                UserPreference(requireContext()).apply {
                    removeLogin()
                    removeUser()
                }

                startActivity(Intent(requireContext(), HomeActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}