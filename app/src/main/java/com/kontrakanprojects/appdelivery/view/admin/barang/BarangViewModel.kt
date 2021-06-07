package com.kontrakanprojects.appdelivery.view.admin.barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarangViewModel : ViewModel() {

    private var _Barang: MutableLiveData<ResponseDetailBarang>? = null

    private val TAG = BarangViewModel::class.simpleName

    fun listBarang(): LiveData<ResponseDetailBarang> {
        _Barang = MutableLiveData<ResponseDetailBarang>()
        Barang()
        return _Barang as MutableLiveData<ResponseDetailBarang>
    }

    fun detalBarang(idDetailBarang: Int): LiveData<ResponseDetailBarang> {
        _Barang = MutableLiveData<ResponseDetailBarang>()
        Barang(idDetailBarang)
        return _Barang as MutableLiveData<ResponseDetailBarang>
    }

    private fun Barang(idDetailBarang: Int? = null) {
        val client: Call<ResponseDetailBarang> = if (idDetailBarang == null) {
            ApiConfig.getApiService().listDetailBarang()
        } else {
            ApiConfig.getApiService().detailBarang(idDetailBarang)
        }

        client.enqueue(object : Callback<ResponseDetailBarang> {
            override fun onResponse(
                call: Call<ResponseDetailBarang>,
                response: Response<ResponseDetailBarang>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _Barang?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailKurir = ResponseDetailBarang(message = message, status = status)
                    _Barang?.postValue(responseDetailKurir)

                    Log.e(TAG, "onFailure: $responseDetailKurir")
                }
            }

            override fun onFailure(call: Call<ResponseDetailBarang>, t: Throwable) {
                _Barang?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}