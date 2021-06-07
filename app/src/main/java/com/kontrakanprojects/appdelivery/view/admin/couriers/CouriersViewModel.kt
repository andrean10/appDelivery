package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        kurir(idKurir, isDetailKurir = true)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun addKurir(
        params: HashMap<String, RequestBody>,
        imagesParams: MultipartBody.Part,
    ): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir(params = params, imagesParams = imagesParams)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun editKurir(idKurir: Int, params: HashMap<String, Any>): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
//        kurir(idKurir, params)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun deleteKurir(idKurir: Int): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
//        kurir(idKurir)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    private fun kurir(
        idKurir: Int? = null,
        params: HashMap<String, RequestBody>? = null,
        imagesParams: MultipartBody.Part? = null,
        isDetailKurir: Boolean = false,
    ) {
        lateinit var client: Call<ResponseKurir>

        if (idKurir == null) {
            if (params != null) {

                Log.d(TAG, "kurir: $params")
                client = ApiConfig.getApiService().addKurir(params, imagesParams) // tambah data
            } else {
                client = ApiConfig.getApiService().listKurir() // list data
            }
        } else {
            if (params != null) {
//                ApiConfig.getApiService().editKurir(idKurir, params) // edit data
            } else {
                if (isDetailKurir) {
                    client = ApiConfig.getApiService().detailKurir(idKurir) // detail data
//                } else {
//                    ApiConfig.getApiService().deleteKurir(idKurir) // hapus data
                }
            }
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