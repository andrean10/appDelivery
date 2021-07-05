package com.kontrakanprojects.appdelivery.view.courier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.appdelivery.databinding.ActivityKurirBarangBinding
import com.kontrakanprojects.appdelivery.view.courier.barang.adapter.ViewPagerAdapter

class KurirBarangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKurirBarangBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKurirBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)
    }
}