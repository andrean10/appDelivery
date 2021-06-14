package com.kontrakanprojects.appdelivery.view.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentAdminProfileBinding
import com.kontrakanprojects.appdelivery.model.profile.ResultsItem
import com.kontrakanprojects.appdelivery.utils.showMessage
import www.sanju.motiontoast.MotionToast

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AdminProfileViewModel>()
    private var idAdmin = 0

    companion object {
        private const val NAMALENGKAP_NOT_NULL = "Nama Lengkap tidak boleh kosong!"
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

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

        idAdmin = AdminProfileFragmentArgs.fromBundle(arguments as Bundle).idAdmin
        observe(idAdmin)

        // save
        with(binding) {
            btnSaveProfileAdmin.setOnClickListener {
                prepareEdit()
            }
        }
    }

    private fun prepareEdit() {
        with(binding) {
            // clear error
            til001.error = null
            til002.error = null
            til003.error = null

            val namaLengkap = etNamaLengkapAdmin.text.toString().trim()
            val username = etUsernameAdmin.text.toString().trim()
            val password = etPasswordAdmin.text.toString().trim()

            when {
                namaLengkap.isEmpty() -> {
                    til001.error = NAMALENGKAP_NOT_NULL
                    return@with
                }
                username.isEmpty() -> {
                    til002.error = USERNAME_NOT_NULL
                    return@with
                }
                password.isEmpty() -> {
                    til003.error = PASSWORD_NOT_NULL
                    return@with
                }
                password.length < 5 -> {
                    til003.error = MIN_COUNTER_LENGTH_PASS
                    return@with
                }
                else -> {
                    val params = HashMap<String, String>()
                    params["nama_lengkap"] = namaLengkap
                    params["username"] = username
                    params["password"] = password

                    viewModel.editAdmin(idAdmin, params).observe(viewLifecycleOwner, { response ->
                        if (response != null) {
                            if (response.status == 200) {
                                showMessage(requireActivity(), getString(R.string.success),
                                    response.message!!, MotionToast.TOAST_SUCCESS)
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
            }
        }
    }

    private fun observe(idAdmin: Int) {
        viewModel.detailAdmin(idAdmin).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results?.get(0)

                    if (result != null) {
                        prepare(result)
                    }
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
        with(binding) {
            etNamaLengkapAdmin.setText(result.namaLengkap)
            etUsernameAdmin.setText(result.username)
            etPasswordAdmin.setText(result.password)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}