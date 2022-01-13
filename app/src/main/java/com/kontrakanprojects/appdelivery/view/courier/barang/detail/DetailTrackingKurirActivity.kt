package com.kontrakanprojects.appdelivery.view.courier.barang.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityDetailTrackingKurirBinding
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.sessions.UserPreference

class DetailTrackingKurirActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTrackingKurirBinding

    private var customer_code: String? = null
    private var recipient_name: String? = null
    private var phone_number: String? = null
    private var address: String? = null
    private var item_status: String? = null
    private var received_photo: String? = null

    companion object {
        const val EXTRA_ID_BARANG = "extra_id"
        const val EXTRA_KODE_PELANGGAN = "extra_kode_pelanggan"
        const val EXTRA_NAMA_PENERIMA = "extra_nama_penerima"
        const val EXTRA_NOMOR_HP = "extra_nomor_hp"
        const val EXTRA_ALAMAT = "extra_alamat"
        const val EXTRA_STATUS_BARANG = "extra_status_barang"
        const val EXTRA_FOTO_DITERIMA = "extra_foto_diterima"
        private const val REQUEST_CODE_PERMISSIONS = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTrackingKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarTitle()

        customer_code = intent.getIntExtra(EXTRA_KODE_PELANGGAN, 0).toString()
        recipient_name = intent.getStringExtra(EXTRA_NAMA_PENERIMA)
        phone_number = intent.getStringExtra(EXTRA_NOMOR_HP)
        address = intent.getStringExtra(EXTRA_ALAMAT)
        item_status = intent.getStringExtra(EXTRA_STATUS_BARANG)
        received_photo = intent.getStringExtra(EXTRA_FOTO_DITERIMA)

        result()
    }

    private fun result() {
        with(binding){
            when(item_status){
                "1" -> {
                    tvStatusPackage.text = getString(R.string.rb_001)
                }
                "2" -> {
                    tvStatusPackage.text = getString(R.string.rb_002)
                }
                "3" -> {
                    tvStatusPackage.text = getString(R.string.rb_003)
                }
                "4" -> {
                    tvStatusPackage.text = getString(R.string.rb_004)
                }
                "5" -> {
                    tvStatusPackage.text = getString(R.string.rb_005)
                }
                "6" -> {
                    tvStatusPackage.text = getString(R.string.rb_006)
                }
            }
            tvCodeCostumer.text = customer_code
            tvNameCostumer.text = recipient_name
            tvNumberPhone.text = phone_number
            tvAddressCostumer.text = address

            Glide.with(this@DetailTrackingKurirActivity)
                .load(ApiConfig.IMG_URL + received_photo)
                .placeholder(R.drawable.no_profile_images)
                .error(R.drawable.no_profile_images)
                .into(ivImageDeliver)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        setSupportActionBar(binding.topAppBar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Detail Tracking Barang"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }


}