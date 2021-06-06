package com.kontrakanprojects.appdelivery.view.admin.couriers.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailCouriersBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.CouriersViewModel
import www.sanju.motiontoast.MotionToast

class DetailCouriersFragment : Fragment() {

    private var _binding: FragmentDetailCouriersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CouriersViewModel>()
    private var idKurir = 0

    private val TAG = DetailCouriersFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailCouriersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idKurir = DetailCouriersFragmentArgs.fromBundle(arguments as Bundle).idKurir
        init()
    }

    private fun init() {
        viewModel.detailKurir(idKurir).observe(viewLifecycleOwner, { response ->
//            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    prepare(result?.get(0))
                } else {
                    showMessage(requireActivity(),
                        "Not Found",
                        response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else { // failed mengambil data
                showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepare(result: ResultKurir?) {
        if (result != null) {
            with(binding) {
                Glide.with(requireActivity())
                    .load(ApiConfig.URL + result.fotoProfil)
                    .placeholder(R.drawable.no_profile_images)
                    .error(R.drawable.no_profile_images)
                    .into(ivCourierPhoto)

                etNamaLengkapKurir.setText(result.namaLengkap)
                etUsername.setText(result.username)
                etPassword.setText(result.password)
                etAlamatLengkapKurir.setText(result.alamat)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}