package com.kontrakanprojects.appdelivery.view.admin.barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarangViewModel : ViewModel() {

    private var _location = MutableLiveData<Map<String, String>>()
    val location: LiveData<Map<String, String>>
        get() = _location

    private var _barang: MutableLiveData<ResponseDetailBarang>? = null

    private val TAG = BarangViewModel::class.simpleName

    fun setLocation(latLong: HashMap<String, String>) {
        _location.postValue(latLong)
    }

    fun listBarang(): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        Barang()
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    fun detalBarang(idDetailBarang: Int): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        Barang(idDetailBarang)
        return _barang as MutableLiveData<ResponseDetailBarang>
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
                    _barang?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailKurir = ResponseDetailBarang(message = message, status = status)
                    _barang?.postValue(responseDetailKurir)

                    Log.e(TAG, "onFailure: $responseDetailKurir")
                }
            }

            override fun onFailure(call: Call<ResponseDetailBarang>, t: Throwable) {
                _barang?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}