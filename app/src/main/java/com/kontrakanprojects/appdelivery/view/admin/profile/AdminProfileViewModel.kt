package com.kontrakanprojects.appdelivery.view.admin.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.profile.ResponseProfileAdmin
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileViewModel : ViewModel() {

    private var _admin: MutableLiveData<ResponseProfileAdmin>? = null

    fun detailAdmin(idLogin: Int): LiveData<ResponseProfileAdmin> {
        _admin = MutableLiveData<ResponseProfileAdmin>()
        admin(idLogin)
        return  _admin as MutableLiveData<ResponseProfileAdmin>
    }

    private fun admin(idLogin: Int) {
        val client: Call<ResponseProfileAdmin> = ApiConfig.getApiService().detailAdmin(idLogin)

        client.enqueue(object : Callback<ResponseProfileAdmin> {
            override fun onResponse(
                call: Call<ResponseProfileAdmin>,
                response: Response<ResponseProfileAdmin>
            ) {
                if (response.isSuccessful){
                    val result = response.body()
                    _admin?.postValue(result!!)
                }else{
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseProfileAdmin = ResponseProfileAdmin(message = message, status = status)
                    _admin?.postValue(responseProfileAdmin)

                    Log.e(TAG, "onFailure: $responseProfileAdmin")
                }
            }
            override fun onFailure(call: Call<ResponseProfileAdmin>, t: Throwable) {
                _admin?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}