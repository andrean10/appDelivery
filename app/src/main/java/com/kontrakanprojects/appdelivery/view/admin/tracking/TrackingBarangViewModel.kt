package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingBarangViewModel : ViewModel() {

    private var _tracking: MutableLiveData<ResponseTrackings>? = null

    private var _tracking1: MutableLiveData<ResponseTracking>? = null

    private val TAG = TrackingBarangViewModel::class.simpleName

    fun listTracking(): LiveData<ResponseTrackings> {
        _tracking = MutableLiveData<ResponseTrackings>()
        tracking()
        return _tracking as MutableLiveData<ResponseTrackings>
    }

    fun listTrackingbrg(idBarang: Int): LiveData<ResponseTracking>{
        _tracking1 = MutableLiveData<ResponseTracking>()
        tracking1(idBarang)
        return  _tracking1 as MutableLiveData<ResponseTracking>
    }

    private fun tracking1(idBarang: Int) {
        val client: Call<ResponseTracking> = ApiConfig.getApiService().listTrackingbrg(idBarang)

        client.enqueue(object : Callback<ResponseTracking> {
            override fun onResponse(
                call: Call<ResponseTracking>,
                response: Response<ResponseTracking>
            ) {
                if (response.isSuccessful){
                    val result = response.body()
                    _tracking1?.postValue(result!!)
                }else{
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseTracking = ResponseTracking(message = message, status = status)
                    _tracking1?.postValue(responseTracking)

                    Log.e(TAG, "onFailure: $responseTracking")
                }
            }

            override fun onFailure(call: Call<ResponseTracking>, t: Throwable) {
                _tracking1?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun tracking(idDetailBarang: Int? = null) {
        val client: Call<ResponseTrackings> = ApiConfig.getApiService().listTracking()

        client.enqueue(object : Callback<ResponseTrackings> {
            override fun onResponse(
                call: Call<ResponseTrackings>,
                response: Response<ResponseTrackings>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _tracking?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseTrackings = ResponseTrackings(message = message, status = status)
                    _tracking?.postValue(responseTrackings)

                    Log.e(TAG, "onFailure: $responseTrackings")
                }
            }

            override fun onFailure(call: Call<ResponseTrackings>, t: Throwable) {
                _tracking?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}