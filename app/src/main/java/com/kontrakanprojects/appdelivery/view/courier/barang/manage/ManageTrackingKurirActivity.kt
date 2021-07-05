package com.kontrakanprojects.appdelivery.view.courier.barang.manage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityManageTrackingKurirBinding
import com.kontrakanprojects.appdelivery.db.LatLong
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.courier.viewmodel.BarangKurirViewModel
import www.sanju.motiontoast.MotionToast

class ManageTrackingKurirActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageTrackingKurirBinding
    private val viewModel by viewModels<BarangKurirViewModel>()

    private var idBarang = 0
    private var statusBarang = 0
    private var longitude: String? = null
    private var latitude: String? = null
    private var isValid = true

    companion object {
        const val EXTRA_ID_BARANG = "extra_id"
        const val EXTRA_STATUS_BARANG = "extra_status_barang"
    }

    private val TAG = ManageTrackingKurirActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTrackingKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarTitle()

        idBarang = intent.getIntExtra(EXTRA_ID_BARANG, 0)
        statusBarang = intent.getIntExtra(EXTRA_STATUS_BARANG, 0)

        prepareEdit()

        with(binding) {
            btnSavePackage.setOnClickListener { validation() }
            btnChooseLocation.setOnClickListener { moveMaps() }
        }
    }

    private fun moveMaps() {
        startActivityForResult(Intent(this, MapsActivity::class.java), MapsActivity.REQUEST_MAPS)
    }

    private fun validation() {
        with(binding) {
            // cek nilai yang dikirimkan
            // cek radio button
            when {
                rbTrack02.isChecked -> {
                    statusBarang = 2
                }
                rbTrack03.isChecked -> {
                    statusBarang = 3
                }
                rbTrack04.isChecked -> {
                    statusBarang = 4
                }
                else -> {
                    isValid = false
                }
            }

            // cek location
            when {
                longitude.isNullOrEmpty() -> {
                    tvFailedLocation.visibility = View.VISIBLE
                    isValid = false
                }
                latitude.isNullOrEmpty() -> {
                    tvFailedLocation.visibility = View.VISIBLE
                    isValid = false
                }
            }

            if (isValid) {
                val params = getUserDetail()

                Log.d(TAG, "validation: $params")

                addTrackingBarang(params)
                tvFailedLocation.visibility = View.GONE
            } else {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.failed),
                    "Data masih ada yang kosong!", MotionToast.TOAST_WARNING)
            }
        }
    }

    private fun addTrackingBarang(params: HashMap<String, String>) {
        isLoading(true)
        viewModel.addTracking(params).observe(this@ManageTrackingKurirActivity, { response ->
            isLoading(false)
            if (response.status == 200) {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.success),
                    "Berhasil mengubah status barang", MotionToast.TOAST_SUCCESS)
                finish()
            } else {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.success),
                    response.message, MotionToast.TOAST_ERROR)
                finish()
            }
        })
    }

    private fun prepareEdit() {
        with(binding) {
            when (statusBarang) {
                2 -> {
                    rbTrack02.visibility = View.GONE
                }
                3 -> {
                    rbTrack02.visibility = View.GONE
                    rbTrack03.visibility = View.GONE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MapsActivity.REQUEST_MAPS && resultCode == RESULT_OK && data != null) {
            val latLong = data.getParcelableExtra<LatLong>(MapsActivity.RESULT_LATLONG)
            if (latLong != null) {
                longitude = latLong.longitude.toString()
                latitude = latLong.latitude.toString()

                binding.tvLocationLatlong.text = getString(R.string.latlong, latitude, longitude)
            }
        }
    }

    private fun isLoading(state: Boolean) {
        with(binding) {
            if (state) {
                pbSave.visibility = View.VISIBLE
                btnSavePackage.visibility = View.GONE
            } else {
                pbSave.visibility = View.GONE
                btnSavePackage.visibility = View.VISIBLE
            }
        }
    }

    private fun getUserDetail(): HashMap<String, String> {
        return hashMapOf(
            "id_barang" to idBarang.toString(),
            "detail" to statusBarang.toString(),
            "longitude" to longitude.toString(),
            "latitude" to latitude.toString()
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        setSupportActionBar(binding.topAppBar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Ubah Status Barang"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}