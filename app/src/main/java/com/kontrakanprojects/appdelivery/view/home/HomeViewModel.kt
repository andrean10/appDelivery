package com.kontrakanprojects.appdelivery.view.home

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

class HomeViewModel : ViewModel() {

    private var _search: MutableLiveData<ResponseDetailBarang>? = null

    private val TAG = HomeViewModel::class.simpleName

    fun search(kodeResi: String): LiveData<ResponseDetailBarang> {
        _search = MutableLiveData<ResponseDetailBarang>()
        getSearch(kodeResi)
        return _search as MutableLiveData<ResponseDetailBarang>
    }

    private fun getSearch(kodeResi: String) {
        val client = ApiConfig.getApiService().barang(kodeResi)
        client.enqueue(object : Callback<ResponseDetailBarang> {
            override fun onResponse(
                call: Call<ResponseDetailBarang>,
                response: Response<ResponseDetailBarang>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _search?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAuth = ResponseDetailBarang(message = message, status = status)
                    _search?.postValue(responseAuth)

                    Log.e(TAG, "onFailure: $responseAuth")
                }
            }

            override fun onFailure(call: Call<ResponseDetailBarang>, t: Throwable) {
                _search?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}