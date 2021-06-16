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
        imagesParams: MultipartBody.Part?,
    ): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir(paramsTambahKurir = params, imagesParams = imagesParams)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun editKurir(idKurir: Int, params: HashMap<String, String>): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir(idKurir, paramsEditKurir = params)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun editPhotoProfile(idKurir: Int, imagesParams: MultipartBody.Part): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        editPhotoKurir(idKurir, imagesParams)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun deletePhotoProfile(idKurir: Int): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        editPhotoKurir(idKurir)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    fun deleteKurir(idKurir: Int): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir(idKurir)
        return _kurir as MutableLiveData<ResponseKurir>
    }

    private fun kurir(
        idKurir: Int? = null,
        paramsTambahKurir: HashMap<String, RequestBody>? = null,
        paramsEditKurir: HashMap<String, String>? = null,
        imagesParams: MultipartBody.Part? = null,
        isDetailKurir: Boolean = false,
    ) {

        val client: Call<ResponseKurir> = if (idKurir == null) {
            if (paramsTambahKurir != null) {
                ApiConfig.getApiService().addKurir(paramsTambahKurir, imagesParams) // tambah data
            } else {
                ApiConfig.getApiService().listKurir() // list data
            }
        } else {
            if (paramsEditKurir != null) {
                ApiConfig.getApiService().editKurir(idKurir, paramsEditKurir) // edit data
            } else {
                if (isDetailKurir) {
                    ApiConfig.getApiService().detailKurir(idKurir) // detail data
                } else {
                    ApiConfig.getApiService().deleteKurir(idKurir) // hapus data
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
                    val status = JSONObject(errResult ?: "500").getInt("status")
                    val message =
                        JSONObject(errResult ?: "Internal Server Error").getString("message")
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

    private fun editPhotoKurir(idKurir: Int, imagesParams: MultipartBody.Part? = null) {
        val client = if (imagesParams != null) {
            ApiConfig.getApiService().editPhotoProfil(idKurir, imagesParams)
        } else {
            ApiConfig.getApiService().deletePhotoProfil(idKurir)
        }

        client.enqueue(object : Callback<ResponseKurir> {
            override fun onResponse(call: Call<ResponseKurir>, response: Response<ResponseKurir>) {
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