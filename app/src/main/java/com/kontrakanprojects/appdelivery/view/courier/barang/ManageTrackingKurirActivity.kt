package com.kontrakanprojects.appdelivery.view.courier.barang

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityManageTrackingKurirBinding
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.utils.showMessage
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.HashMap

class ManageTrackingKurirActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageTrackingKurirBinding
    private var viewModel: BarangKurirViewModel? = null
//    private var input: HashMap<String, String>? = null
    private var id_barang: String = "id_barang"
    private var b: String? = null

    private var _tracking: MutableLiveData<ResponseTracking>? = null

    private val TAG = BarangKurirViewModel::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelProvider.NewInstanceFactory()).get(BarangKurirViewModel::class.java)
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

        if (binding.rbTrack01.isEnabled == true){
            b = "1"
        }else if (binding.rbTrack02.isEnabled == true){
            b = "2"
        }else if (binding.rbTrack03.isEnabled == true){
            b = "3"
        }else if (binding.rbTrack04.isEnabled == true){
            b = "4"
        }

        _tracking = MutableLiveData<ResponseTracking>()
        binding.btnSavePackage.setOnClickListener {
            val a = getUserDetail(b, statusBarang)

            viewModel!!.addTracking(a).observe(this, { response ->
                if (response.status == 200) {
                    Toast.makeText(this, "Berhasul", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

//            val client = ApiConfig.getApiService().addTracking(a)

//            client.enqueue(object : Callback<ResponseTracking> {
//                override fun onResponse(
//                    call: Call<ResponseTracking>,
//                    response: Response<ResponseTracking>
//                ) {
//                    if (response.isSuccessful) {
//                        val result = response.body()
//                        _tracking?.postValue(result!!)
//                    } else {
//                        val errResult = response.errorBody()?.string()
//                        Log.d("tsa", "onResponse: $errResult")
//                        val status = JSONObject(errResult!!).getInt("status")
//                        val message = JSONObject(errResult).getString("message")
//                        val responseTracking = ResponseTracking(message = message, status = status)
//                        _tracking?.postValue(responseTracking)
//
//                        Log.e("testing", "onFailure: $responseTracking")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseTracking>, t: Throwable) {
//                    _tracking?.postValue(null)
//                    Log.e("testing", "onFailure: ${t.message}")
//                }
//            })
        }
    }

    private fun getUserDetail(choose: String?, status: String?): HashMap<String, String> {
        val user: HashMap<String, String> = HashMap()
        user["detail"] = choose!!
        user["longitude"] = "01221732"
        user["latitude"] = "217887238"
        user["id_barang"] = status!!
//        Log.d("tesaaa", "getUserDetail: $user")
        return user
    }

}