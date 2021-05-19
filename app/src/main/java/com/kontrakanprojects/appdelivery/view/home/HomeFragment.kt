package com.kontrakanprojects.appdelivery.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.databinding.FragmentHomeBinding
import com.kontrakanprojects.appdelivery.views.LoginActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // pindah ke auth navigation
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

}