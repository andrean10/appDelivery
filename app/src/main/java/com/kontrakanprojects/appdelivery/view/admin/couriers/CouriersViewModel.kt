package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouriersViewModel : ViewModel() {

    private var _kurir: MutableLiveData<ResponseKurir>? = null

    private val TAG = CouriersViewModel::class.simpleName

    fun listKurir(): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir()
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun detailKurir(idKurir: Int): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir(idKurir)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    private fun kurir(idKurir: Int? = null) {
        val client: Call<ResponseKurir> = if (idKurir == null) {
            ApiConfig.getApiService().listKurir()
        } else {
            ApiConfig.getApiService().detailKurir(idKurir)
        }

        client.enqueue(object : Callback<ResponseKurir> {
            override fun onResponse(
                call: Call<ResponseKurir>,
                response: Response<ResponseKurir>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _kurir?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseKurir = ResponseKurir(message = message, status = status)
                    _kurir?.postValue(responseKurir)

                    Log.e(TAG, "onFailure: $responseKurir")
                }
            }

            override fun onFailure(call: Call<ResponseKurir>, t: Throwable) {
                _kurir?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}