package com.kontrakanprojects.appdelivery.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private var _search: MutableLiveData<ResponseTracking>? = null

    private val TAG = HomeViewModel::class.simpleName

    fun search(kodeResi: Int): LiveData<ResponseTracking> {
        _search = MutableLiveData<ResponseTracking>()
        getSearch(kodeResi)
        return _search as MutableLiveData<ResponseTracking>
    }

    private fun getSearch(kodeResi: Int) {
        val client = ApiConfig.getApiService().tracking(kodeResi)
        client.enqueue(object : Callback<ResponseTracking> {
            override fun onResponse(
                call: Call<ResponseTracking>,
                response: Response<ResponseTracking>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _search?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseTracking = ResponseTracking(message = message, status = status)
                    _search?.postValue(responseTracking)

                    Log.e(TAG, "onFailure: $responseTracking")
                }
            }

            override fun onFailure(call: Call<ResponseTracking>, t: Throwable) {
                _search?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}