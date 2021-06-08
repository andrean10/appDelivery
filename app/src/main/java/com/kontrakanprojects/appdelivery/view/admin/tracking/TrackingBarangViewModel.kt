package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingBarangViewModel : ViewModel() {

    private var _tracking: MutableLiveData<ResponseTrackings>? = null

    private val TAG = TrackingBarangViewModel::class.simpleName

    fun listTracking(): LiveData<ResponseTrackings> {
        _tracking = MutableLiveData<ResponseTrackings>()
        tracking()
        return _tracking as MutableLiveData<ResponseTrackings>
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