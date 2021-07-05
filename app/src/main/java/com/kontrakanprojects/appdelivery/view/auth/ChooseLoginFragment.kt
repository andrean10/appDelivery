package com.kontrakanprojects.appdelivery.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentChooseLoginBinding

class ChooseLoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentChooseLoginBinding

    companion object {
        const val ROLE_ADMIN = 2
        const val ROLE_COURIER = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLoginAsAdmin.setOnClickListener(this@ChooseLoginFragment)
            btnLoginAsCourier.setOnClickListener(this@ChooseLoginFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login_as_admin -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment()
                toLogin.idRole = ROLE_ADMIN
                findNavController().navigate(toLogin)
            }
            R.id.btn_login_as_courier -> {
                val toLogin =
                    ChooseLoginFragmentDirections.actionChooseLoginFragmentToLoginFragment()
                toLogin.idRole = ROLE_COURIER
                findNavController().navigate(toLogin)
            }
        }
    }
}