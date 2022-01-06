package com.kontrakanprojects.appdelivery.view.courier.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.kurir.ResponseBarangKurir
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarangKurirViewModel : ViewModel() {

    private var _barang: MutableLiveData<ResponseBarangKurir>? = null
    private var _tracking: MutableLiveData<ResponseTracking>? = null

    private val TAG = BarangKurirViewModel::class.simpleName

    fun detailKurir(idBarang: Int): LiveData<ResponseBarangKurir> {
        _barang = MutableLiveData<ResponseBarangKurir>()
        barangKurir(idBarang)
        return _barang as MutableLiveData<ResponseBarangKurir>
    }

    fun addTracking(
        params: HashMap<String, RequestBody>,
        imageParams: MultipartBody.Part? = null,
    ): LiveData<ResponseTracking> {
        _tracking = MutableLiveData<ResponseTracking>()
        trackings(params = params, imageParams)
        return _tracking as MutableLiveData<ResponseTracking>
    }

    private fun trackings(params: HashMap<String, RequestBody>, imageParams: MultipartBody.Part?) {
        val client = ApiConfig.getApiService().addTracking(params, imageParams)

        client.enqueue(object : Callback<ResponseTracking> {
            override fun onResponse(
                call: Call<ResponseTracking>,
                response: Response<ResponseTracking>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _tracking?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseTracking = ResponseTracking(message = message, status = status)
                    _tracking?.postValue(responseTracking)

                    Log.e(TAG, "onFailure: $responseTracking")
                }
            }

            override fun onFailure(call: Call<ResponseTracking>, t: Throwable) {
                _tracking?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun barangKurir(idBarang: Int) {
        val client = ApiConfig.getApiService().listDataBarangAdmin(idBarang)

        client.enqueue(object : Callback<ResponseBarangKurir> {
            override fun onResponse(
                call: Call<ResponseBarangKurir>,
                response: Response<ResponseBarangKurir>,
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