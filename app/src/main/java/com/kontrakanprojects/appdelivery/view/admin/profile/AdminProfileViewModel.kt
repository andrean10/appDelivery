package com.kontrakanprojects.appdelivery.view.admin.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.profile.ResponseProfileDetail
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileViewModel : ViewModel() {

    private var _admin: MutableLiveData<ResponseProfileDetail>? = null

    private val TAG = AdminProfileViewModel::class.simpleName

    fun detailAdmin(idAdmin: Int): LiveData<ResponseProfileDetail> {
        _admin = MutableLiveData<ResponseProfileDetail>()
        admin(idAdmin, isDetailAdmin = true)
        return _admin as MutableLiveData<ResponseProfileDetail>
    }

    fun editAdmin(idAdmin: Int, params: HashMap<String, String>): LiveData<ResponseProfileDetail> {
        _admin = MutableLiveData<ResponseProfileDetail>()
        admin(idAdmin, params)
        return _admin as MutableLiveData<ResponseProfileDetail>
    }

    private fun admin(
        idAdmin: Int,
        params: HashMap<String, String>? = null,
        isDetailAdmin: Boolean = false,
    ) {
        val client: Call<ResponseProfileDetail> = if (isDetailAdmin) {
            ApiConfig.getApiService().detailProfileAdmin(idAdmin) // detail data
        } else {
            ApiConfig.getApiService().editProfileAdmin(idAdmin, params!!) // edit data
        }

        client.enqueue(object : Callback<ResponseProfileDetail> {
            override fun onResponse(
                call: Call<ResponseProfileDetail>,
                response: Response<ResponseProfileDetail>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _admin?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseProfileDetail =
                        ResponseProfileDetail(message = message, status = status)
                    _admin?.postValue(responseProfileDetail)

                    Log.e(TAG, "onFailure: $responseProfileDetail")
                }
            }

            override fun onFailure(call: Call<ResponseProfileDetail>, t: Throwable) {
                _admin?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}