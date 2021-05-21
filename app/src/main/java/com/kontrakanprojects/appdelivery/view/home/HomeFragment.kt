package com.kontrakanprojects.appdelivery.view.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontakanprojects.apptkslb.utils.showMessage
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentHomeBinding
import com.kontrakanprojects.appdelivery.view.auth.AuthActivity
import www.sanju.motiontoast.MotionToast

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
                viewModel.search(inputResi).observe(viewLifecycleOwner, { response ->
                    isLoading(false)
                    if (response != null) {
                        if (response.status == 200) {
                            val result = response.results
                            homeAdapter.setData(result)
                        } else {
                            // tambahkan animasi data kosong
                        }
                    } else { // failed mengambil data
                        showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
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
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
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
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Tracking Barang"
        }
    }

}