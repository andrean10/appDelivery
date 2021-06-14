package com.kontrakanprojects.appdelivery.view.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentAdminProfileBinding
import com.kontrakanprojects.appdelivery.model.profile.ResultsAdmin
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragment
import com.kontrakanprojects.appdelivery.view.admin.tracking.DetailTrackingAdapter
import com.kontrakanprojects.appdelivery.view.admin.tracking.TrackingBarangViewModel
import www.sanju.motiontoast.MotionToast

class AdminProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AdminProfileViewModel>()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var etUiUpdated: TextInputEditText

    private var idLogin = 0

    private lateinit var bottomSheetView: View

    private val TAG = AdminProfileFragment::class.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = AdminProfileFragmentArgs.fromBundle(arguments as Bundle)
        idLogin = args.idAdmin
        init()
        with(binding){
            etNamaLengkap.setOnClickListener(this@AdminProfileFragment)
            btnSaveProfileAdmin.setOnClickListener(this@AdminProfileFragment)
        }
    }

    private fun init(){
        with(binding){
            viewModel.detailAdmin(idLogin).observe(viewLifecycleOwner, {response ->
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        prepare(result?.get(0))
                    }else{
                        showMessage(requireActivity(),
                            "Not Found",
                            response.message,
                            MotionToast.TOAST_ERROR)
                    }
                }else {
                    showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun prepare(result: ResultsAdmin?) {
        if (result != null) {
            with(binding) {
                etNamaLengkap.setText(result.namaLengkap)
                etUsernameAdmin.setText(result.username)
                etPasswordAdmin.setText(result.password)
            }
        }
    }

    override fun onClick(v: View?) {
        with(binding){
            when(v?.id){
                R.id.btn_save_profile_admin -> {
                    Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}