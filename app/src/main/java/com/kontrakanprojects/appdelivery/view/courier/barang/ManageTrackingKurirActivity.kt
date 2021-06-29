package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.appdelivery.databinding.ActivityManageTrackingKurirBinding
import com.kontrakanprojects.appdelivery.view.courier.viewmodel.BarangKurirViewModel

class ManageTrackingKurirActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageTrackingKurirBinding
    private val viewModel by viewModels<BarangKurirViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTrackingKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val idBarang = intent.getStringExtra("id_barang")
        val statusBarang = intent.getStringExtra("status_barang")

        Log.d("WTF", "onCreate: $idBarang")
        binding.etIdBarang.text = idBarang

        when (statusBarang) {
            "1" -> {
                binding.rbTrack01.isEnabled = false
            }
            "2" -> {
                binding.rbTrack01.isEnabled = false
                binding.rbTrack02.isEnabled = false
            }
            "3" -> {
                binding.rbTrack01.isEnabled = false
                binding.rbTrack02.isEnabled = false
                binding.rbTrack03.isEnabled = false
            }
        }
    }
}