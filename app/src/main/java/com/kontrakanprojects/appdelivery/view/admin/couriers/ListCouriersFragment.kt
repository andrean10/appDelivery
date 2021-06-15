package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        with(binding) {
            courierAdapter = ListCouriersAdapter()
            with(rvCourierList) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = courierAdapter
            }

            isLoading(true)
            viewModel.listKurir().observe(viewLifecycleOwner, { response ->
                isLoading(false)
                if (response != null) {
                    if (response.status == 200) {
                        isNotEmptyData(true)
                        val result = response.results
                        courierAdapter.setData(result)
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
            if (status) {
                pbLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
            }
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
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "List Kurir"
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