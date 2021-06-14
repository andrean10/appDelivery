package com.kontrakanprojects.appdelivery.view.admin.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.profile.ResponseProfileDetail
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    private var _profile: MutableLiveData<ResponseProfileDetail>? = null

    private val TAG = DashboardViewModel::class.simpleName

    fun profile(idProfile: Int, idRole: Int): LiveData<ResponseProfileDetail> {
        _profile = MutableLiveData<ResponseProfileDetail>()
        getProfile(idProfile, idRole)
        return _profile as MutableLiveData<ResponseProfileDetail>
    }

    private fun getProfile(idProfile: Int, idRole: Int) {
        val client: Call<ResponseProfileDetail>? = when (idRole) {
            ChooseLoginFragment.ROLE_ADMIN -> ApiConfig.getApiService()
                .detailProfileAdmin(idProfile)
            ChooseLoginFragment.ROLE_COURIER -> ApiConfig.getApiService()
                .detailProfileKurir(idProfile)
            else -> null
        }

        client?.enqueue(object : Callback<ResponseProfileDetail> {
            override fun onResponse(
                call: Call<ResponseProfileDetail>,
                response: Response<ResponseProfileDetail>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _profile?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseProfileDetail =
                        ResponseProfileDetail(message = message, status = status)
                    _profile?.postValue(responseProfileDetail)

                    Log.e(TAG, "onFailure: $responseProfileDetail")
                }
            }

            override fun onFailure(call: Call<ResponseProfileDetail>, t: Throwable) {
                _profile?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}