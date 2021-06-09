package com.kontrakanprojects.appdelivery.view.admin.barang.managebarang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kontrakanprojects.appdelivery.databinding.FragmentManageBarangBinding
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel

// Class untuk CRUD Barang
class ManageBarangFragment : Fragment() {

    private lateinit var binding: FragmentManageBarangBinding
    private val viewModel by viewModels<BarangViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}