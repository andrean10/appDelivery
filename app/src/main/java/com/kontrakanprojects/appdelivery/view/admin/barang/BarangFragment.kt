package com.kontrakanprojects.appdelivery.view.admin.barang

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.databinding.FragmentBarangBinding
import com.kontrakanprojects.appdelivery.databinding.FragmentListCouriersBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.ListCouriersAdapter
import com.kontrakanprojects.appdelivery.view.admin.couriers.ListCouriersFragmentDirections
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

        binding.fabAddProduct.setOnClickListener {
//            findNavController().navigate(R.id.actionlist)
        }

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
                        isNotEmptyData(true)
                        val result = response.results
                        barangAdapter.setData(result)
                    } else {
                        isNotEmptyData(false)
//                        animationViewImage.visibility = View.VISIBLE
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
//            if (status) {
//                pbLoading.visibility = View.VISIBLE
//                animationViewImage.visibility = View.GONE
//            } else {
//                pbLoading.visibility = View.GONE
//            }
        }
    }

    private fun isNotEmptyData(state: Boolean) {
        with(binding) {
//            if (state) {
//                rvSuccess.visibility = View.VISIBLE
//                rvFailed.visibility = View.GONE
//            } else {
//                rvSuccess.visibility = View.GONE
//                rvFailed.visibility = View.VISIBLE
//            }
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Detail Barang"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}