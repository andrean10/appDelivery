package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kontrakanprojects.appdelivery.R

class FirstProsesFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_proses, container, false)
    }

    companion object {
        fun newInstance(): FirstProsesFragment {
            return FirstProsesFragment()
        }
    }
}