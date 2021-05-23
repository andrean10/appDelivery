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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontrakanprojects.appdelivery.databinding.FragmentLoginBinding
import com.kontrakanprojects.appdelivery.db.Login
import com.kontrakanprojects.appdelivery.db.User
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.view.admin.AdminActivity
import com.kontrakanprojects.appdelivery.view.auth.AuthViewModel
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import com.kontrakanprojects.appdelivery.view.courier.CourierActivity
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
                    if (s?.length!! < 5) {
                        binding.tiPassword.error = MIN_COUNTER_LENGTH_PASS
                    } else if (s.isNullOrEmpty()) {
                        binding.tiPassword.error = PASSWORD_NOT_NULL
                    } else {
                        binding.tiPassword.error = null
                    }
                }
            })

            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when {
                    username.isEmpty() -> tiUsername.error = USERNAME_NOT_NULL
                    password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                    else -> {
                        pbLoading.visibility = View.VISIBLE

                        val params = HashMap<String, Any>()
                        params["username"] = username
                        params["password"] = password
                        params["id_role"] = idRole

                        viewModel.login(params).observe(viewLifecycleOwner, { result ->
                            pbLoading.visibility = View.GONE
                            if (result != null) {
                                if (result.status == 200) {
                                    UserPreference(requireContext()).apply {
                                        setUser(
                                            User(
                                                idUser = result.results?.id,
                                                idRole = result.results?.idRole,
                                                namaUser = result.results?.nama
                                            )
                                        )
                                        setLogin(Login(loginSuccess))
                                    }

                                    when (idRole) {
                                        ChooseLoginFragment.ROLE_ADMIN -> {
                                            val intent = Intent(
                                                requireContext(),
                                                AdminActivity::class.java
                                            )
                                            startActivity(intent)
                                            activity?.finish()

                                            Toast.makeText(requireContext(),
                                                "Admin berhasil login",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                        ChooseLoginFragment.ROLE_COURIER -> {
                                            val intent = Intent(
                                                requireContext(),
                                                CourierActivity::class.java
                                            )
                                            startActivity(intent)
                                            activity?.finish()

                                            Toast.makeText(requireContext(),
                                                "Kurir berhasil login",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    showMessage(requireActivity(),
                                        "Failed",
                                        result.message,
                                        MotionToast.TOAST_ERROR)
                                }
                            }
                        })

                        // hide keyboard
                        hideKeyboard(requireActivity())
                    }
                }
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