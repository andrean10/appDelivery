package com.kontrakanprojects.appdelivery.view.auth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentLoginBinding
import com.kontrakanprojects.appdelivery.db.Login
import com.kontrakanprojects.appdelivery.db.User
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.AdminActivity
import com.kontrakanprojects.appdelivery.view.auth.AuthViewModel
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import www.sanju.motiontoast.MotionToast

class LoginFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private val loginSuccess = true

    companion object {
        private const val USERNAME_NOT_NULL = "Username tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Password tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idRole = LoginFragmentArgs.fromBundle(arguments as Bundle).idRole

        with(binding) {
            textObserve()

            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when {
                    username.isEmpty() -> {
                        tiUsername.error = USERNAME_NOT_NULL
                        return@setOnClickListener
                    }
                    password.isEmpty() -> {
                        tiPassword.error = PASSWORD_NOT_NULL
                        return@setOnClickListener
                    }
                    else -> {
                        isLoading(true)

                        val params = HashMap<String, String>()
                        params["username"] = username
                        params["password"] = password

                        viewModel.login(params).observe(viewLifecycleOwner, { response ->
                            isLoading(false)
                            if (response != null) {
                                if (response.status == 200) {
                                    val result = response.result

                                    UserPreference(requireContext()).apply {
                                        val idLogin =
                                            if (idRole == ChooseLoginFragment.ROLE_ADMIN) {
                                                result?.idLogin ?: 0
                                            } else {
                                                result?.idKurir ?: 0
                                            }

                                        setUser(
                                            User(
                                                idUser = idLogin,
                                                idRole = idRole,
                                                namaUser = result?.namaLengkap ?: "NULL"
                                            )
                                        )
                                        setLogin(Login(loginSuccess))
                                    }

                                    val intent = Intent(requireContext(), AdminActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish()
                                } else {
                                    tvMessageFailed.visibility = View.VISIBLE
                                    tvMessageFailed.text = response.message
                                }
                            } else {
                                showMessage(requireActivity(), getString(R.string.failed),
                                    style = MotionToast.TOAST_ERROR)
                            }
                        })

                        // hide keyboard
                        hideKeyboard(requireActivity())
                    }
                }
            }
        }
    }

    private fun textObserve() {
        with(binding) {
            edtUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! == 0) {
                        binding.tiUsername.error = USERNAME_NOT_NULL
                    } else {
                        binding.tiUsername.error = null
                    }
                }
            })

            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    when {
                        s?.length!! < 5 -> {
                            binding.tiPassword.error = MIN_COUNTER_LENGTH_PASS
                        }
                        s.isNullOrEmpty() -> {
                            binding.tiPassword.error = PASSWORD_NOT_NULL
                        }
                        else -> {
                            binding.tiPassword.error = null
                        }
                    }
                }
            })
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
                tvMessageFailed.visibility = View.GONE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}