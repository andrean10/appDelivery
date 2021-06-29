package com.kontrakanprojects.appdelivery.view.courier.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.kurir.ResponseBarangKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarangKurirViewModel: ViewModel() {

    private var _barang: MutableLiveData<ResponseBarangKurir>? = null

    private val TAG = BarangKurirViewModel::class.simpleName

    fun detailKurir(idBarang: Int): LiveData<ResponseBarangKurir> {
        _barang = MutableLiveData<ResponseBarangKurir>()
        barangKurir(idBarang)
        return _barang as MutableLiveData<ResponseBarangKurir>
    }

    private fun barangKurir(idBarang: Int) {
        val client = ApiConfig.getApiService().listDataBarangAdmin(idBarang)

        client.enqueue(object : Callback<ResponseBarangKurir> {
            override fun onResponse(
                call: Call<ResponseBarangKurir>,
                response: Response<ResponseBarangKurir>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _barang?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseBarangKurir =
                        ResponseBarangKurir(message = message, status = status)
                    _barang?.postValue(responseBarangKurir)

                    Log.e(TAG, "onFailure: $responseBarangKurir")
                }

            }

            override fun onFailure(call: Call<ResponseBarangKurir>, t: Throwable) {
                _barang?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}