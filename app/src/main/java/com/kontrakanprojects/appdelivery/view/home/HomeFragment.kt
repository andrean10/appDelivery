package com.kontrakanprojects.appdelivery.view.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentHomeBinding
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.auth.AuthActivity
import www.sanju.motiontoast.MotionToast

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var homeAdapter: HomeAdapter

    private val TAG = HomeFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()

        with(binding) {
            // set adapter
            homeAdapter = HomeAdapter()
            with(rvWaybill) {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = homeAdapter
            }

            btnSearch.setOnClickListener {
                val inputResi = etCariResi.text.toString().trim()

                if (inputResi.isEmpty()) {
                    tvFailedResi.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                isLoading(true)
                viewModel.search(inputResi.toInt()).observe(viewLifecycleOwner, { response ->
                    isLoading(false)
                    if (response != null) {
                        if (response.status == 200) {
                            isNotEmptyData(true)
                            val result = response.results?.get(0)
                            val listTracking = result?.tracking
                            homeAdapter.setData(listTracking)

                            btnDetail.setOnClickListener {
                                val toDetailBarang =
                                    HomeFragmentDirections.actionHomeFragmentToDetailBarangFragment()
                                toDetailBarang.idBarang = result?.idBarang!!
                                findNavController().navigate(toDetailBarang)
                            }
                        } else {
                            isNotEmptyData(false)
                            animationViewImage.visibility = View.VISIBLE
                        }
                    } else { // failed mengambil data
                        showMessage(requireActivity(),
                            getString(R.string.failed),
                            style = MotionToast.TOAST_ERROR)
                    }
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.login, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // pindah ke auth navigation
        if (item.itemId == R.id.login) {
            startActivity(Intent(requireContext(), AuthActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                pbLoading.visibility = View.VISIBLE
                animationViewImage.visibility = View.GONE
            } else {
                pbLoading.visibility = View.GONE
            }
        }
    }

    private fun isNotEmptyData(state: Boolean) {
        with(binding) {
            if (state) {
                rvSuccess.visibility = View.VISIBLE
                rvFailed.visibility = View.GONE
            } else {
                rvSuccess.visibility = View.GONE
                rvFailed.visibility = View.VISIBLE
            }
        }
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Tracking Barang"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}