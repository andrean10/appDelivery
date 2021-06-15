package com.kontrakanprojects.appdelivery.view.admin.tracking.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentFirstProsesBinding
import com.kontrakanprojects.appdelivery.databinding.FragmentManageTrackingBinding


class ManageFragment : Fragment() {

    private var _binding: FragmentManageTrackingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManageTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }
}