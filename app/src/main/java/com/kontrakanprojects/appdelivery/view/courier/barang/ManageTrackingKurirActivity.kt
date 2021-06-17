package com.kontrakanprojects.appdelivery.view.courier.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityManageTrackingKurirBinding

class ManageTrackingKurirActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageTrackingKurirBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTrackingKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val idBarang = intent.getStringExtra("id_barang")
        val statusBarang = intent.getStringExtra("status_barang")

        Log.d("WTF", "onCreate: $idBarang")
        binding.etIdBarang.setText(idBarang)

        if (statusBarang == "1"){
            binding.rbTrack01.isEnabled = false
        }else if (statusBarang == "2"){
            binding.rbTrack01.isEnabled = false
            binding.rbTrack02.isEnabled = false
        }else if (statusBarang == "3"){
            binding.rbTrack01.isEnabled = false
            binding.rbTrack02.isEnabled = false
            binding.rbTrack03.isEnabled = false
        }
    }
}