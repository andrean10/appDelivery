package com.kontrakanprojects.appdelivery.view.admin.dashboard

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
                gridLayout.visibility = View.GONE
                gridLayoutKurir.visibility = View.VISIBLE
            }

            imgProfile.setOnClickListener(this@DashboardFragment)
            btnCourier.setOnClickListener(this@DashboardFragment)
            btnTracking.setOnClickListener(this@DashboardFragment)
            btnBarang.setOnClickListener(this@DashboardFragment)
            btnBarangByKurir.setOnClickListener(this@DashboardFragment)
            btnLogout.setOnClickListener(this@DashboardFragment)
            btnLogoutByKurir.setOnClickListener(this@DashboardFragment)
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
                .load(ApiConfig.IMG_URL + result.fotoProfil)
                .listener(listenerImage)
                .placeholder(R.drawable.no_profile_images)
                .error(R.drawable.no_profile_images)
                .into(imgProfile)

            tvNameWelcoming.text = getString(R.string.home_admin, result.namaLengkap)
        }
    }

    private val listenerImage = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBarImage.visibility = View.GONE
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBarImage.visibility = View.GONE
            return false
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
                val toAdmin =
                    DashboardFragmentDirections.actionDashboardFragmentToListCouriersFragment()
                findNavController().navigate(toAdmin)
            }
            R.id.btn_tracking -> {
                findNavController().navigate(R.id.action_dashboardFragment_to_trackingBarangFragment)
            }
            R.id.btn_barang -> {
                val toAdmin =
                    DashboardFragmentDirections.actionDashboardFragmentToBarangFragment()
                findNavController().navigate(toAdmin)
            }
            R.id.btn_barang_by_kurir -> {
                val toCourier =
                    DashboardFragmentDirections.actionDashboardFragmentToKurirBarangActivity()
                toCourier.idKurir = user.idUser ?: 0
                findNavController().navigate(toCourier)
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
            R.id.btn_logout_by_kurir -> {
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