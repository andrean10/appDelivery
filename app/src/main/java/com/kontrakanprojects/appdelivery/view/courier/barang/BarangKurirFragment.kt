package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kontrakanprojects.appdelivery.databinding.FragmentBarangKurirBinding

class BarangKurirFragment : Fragment() {

    private var _binding: FragmentBarangKurirBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBarangKurirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter()
    }

    private fun adapter() {
        with(binding){
            viewPager.adapter = ViewPagerAdapter(this@BarangKurirFragment, parentFragmentManager)
        }
    }
}