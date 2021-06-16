package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentListCouriersBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragment
import www.sanju.motiontoast.MotionToast

// Class untuk daftar list courier
class ListCouriersFragment : Fragment() {

    private var _binding: FragmentListCouriersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CouriersViewModel>()
    private lateinit var courierAdapter: ListCouriersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentListCouriersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        setToolbarTitle()
        binding.fabAddCourier.setOnClickListener {
            val toDetailCourier =
                ListCouriersFragmentDirections.actionListCouriersFragmentToDetailCouriersFragment()
            toDetailCourier.idRequest = DetailCouriersFragment.REQUEST_ADD
            findNavController().navigate(toDetailCourier)
        }

        courierAdapter.setOnItemClickCallBack(object : ListCouriersAdapter.OnItemClickCallBack {
            override fun onItemClicked(resultKurir: ResultKurir) {
                val toDetailCourier =
                    ListCouriersFragmentDirections.actionListCouriersFragmentToDetailCouriersFragment()
                        .apply {
                            idKurir = resultKurir.idKurir ?: 0
                            idRequest = DetailCouriersFragment.REQUEST_EDIT
                        }
                if (toDetailCourier.idKurir != 0) findNavController().navigate(toDetailCourier)
            }
        })
    }

    private fun init() {
        isLoading(true)

        with(binding) {
            courierAdapter = ListCouriersAdapter()
            with(rvCourierList) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = courierAdapter
            }

            viewModel.listKurir().observe(viewLifecycleOwner, { response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        val result = response.results
                        courierAdapter.setData(result)
                    } else {
                        showMessage(requireActivity(),
                            getString(R.string.not_found),
                            response.message,
                            MotionToast.TOAST_WARNING)
                    }
                } else { // failed mengambil data
                    showMessage(requireActivity(), getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
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
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Data Kurir"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}