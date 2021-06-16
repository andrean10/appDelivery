 package com.kontrakanprojects.appdelivery.view.admin.barang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.databinding.FragmentBarangBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.managebarang.ManageBarangFragment
import www.sanju.motiontoast.MotionToast

 class BarangFragment : Fragment() {

     private var _binding: FragmentBarangBinding? = null
     private val binding get() = _binding!!
     private val viewModel by viewModels<BarangViewModel>()
     private lateinit var barangAdapter: ListBarangAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setHasOptionsMenu(true)
     }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        init()

        binding.fabAddProduct.setOnClickListener {
            val toManageBarang =
                BarangFragmentDirections.actionBarangFragmentToManageBarangFragment()
            toManageBarang.idRequest = ManageBarangFragment.REQUEST_ADD
            findNavController().navigate(toManageBarang)
        }

        barangAdapter.setOnItemClickCallBack(object : ListBarangAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultDetailBarang: ResultDetailBarang) {
                val toDetailBarang =
                    BarangFragmentDirections.actionBarangFragmentToDetailBarangFragment()
                toDetailBarang.idBarang = resultDetailBarang.idBarang ?: 0
                if (toDetailBarang.idBarang != 0) findNavController().navigate(toDetailBarang)
            }
        })
    }

    private fun init() {
        with(binding) {
            barangAdapter = ListBarangAdapter()
            with(rvAllProduct) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = barangAdapter
            }

            isLoading(true)
            viewModel.listBarang().observe(viewLifecycleOwner, { response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        barangAdapter.setData(result)
                    } else {
                        showMessage(requireActivity(),
                            "Failed",
                            response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Barang"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}