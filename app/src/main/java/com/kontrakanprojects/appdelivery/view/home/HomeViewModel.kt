package com.kontrakanprojects.appdelivery.view.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private var _search: MutableLiveData<ResponseTrackings>? = null

    private val TAG = HomeViewModel::class.simpleName

    fun search(kodeResi: Int): LiveData<ResponseTrackings> {
        _search = MutableLiveData<ResponseTrackings>()
        getSearch(kodeResi)
        return _search as MutableLiveData<ResponseTrackings>
    }

    private fun getSearch(kodeResi: Int) {
        val client = ApiConfig.getApiService().tracking(kodeResi)
        client.enqueue(object : Callback<ResponseTrackings> {
            override fun onResponse(
                call: Call<ResponseTrackings>,
                response: Response<ResponseTrackings>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _search?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseTrackings = ResponseTrackings(message = message, status = status)
                    _search?.postValue(responseTrackings)

                    Log.e(TAG, "onFailure: $responseTrackings")
                }
            }

            override fun onFailure(call: Call<ResponseTrackings>, t: Throwable) {
                _search?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}