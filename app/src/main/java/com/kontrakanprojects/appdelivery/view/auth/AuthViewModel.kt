package com.kontrakanprojects.appdelivery.view.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.auth.ResponseAuth
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private var _login = MutableLiveData<ResponseAuth?>()

    private val TAG = AuthViewModel::class.simpleName

    fun login(params: HashMap<String, String>, idRole: Int): LiveData<ResponseAuth?> {
        _login = getLogin(params, idRole)
        return _login
    }

    private fun getLogin(
        params: HashMap<String, String>,
        idRole: Int,
    ): MutableLiveData<ResponseAuth?> {
        val client: Call<ResponseAuth>? = when (idRole) {
            ChooseLoginFragment.ROLE_ADMIN -> ApiConfig.getApiService().loginAdmin(params)
            ChooseLoginFragment.ROLE_COURIER -> ApiConfig.getApiService().loginKurir(params)
            else -> null
        }

        client?.enqueue(object : Callback<ResponseAuth> {
            override fun onResponse(call: Call<ResponseAuth>, response: Response<ResponseAuth>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _login.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseAuth = ResponseAuth(message = message, status = status)
                    _login.postValue(responseAuth)

                    Log.e(TAG, "onFailure: $responseAuth")
                }
            }

            override fun onFailure(call: Call<ResponseAuth>, t: Throwable) {
                _login.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
        return _login
    }
}