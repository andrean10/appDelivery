package com.kontrakanprojects.appdelivery.view.admin.barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarangViewModel : ViewModel() {

    private var _location = MutableLiveData<Map<String, Any>?>()
    val location: LiveData<Map<String, Any>?>
        get() = _location

    private var _barang: MutableLiveData<ResponseDetailBarang>? = null
    private var _kurir: MutableLiveData<ResponseKurir>? = null

    private val TAG = BarangViewModel::class.simpleName

    fun setLocation(latLong: HashMap<String, Any>?) {
        _location.postValue(latLong)
    }

    fun listKurir(): LiveData<ResponseKurir> {
        _kurir = MutableLiveData<ResponseKurir>()
        kurir()
        return _kurir as MutableLiveData<ResponseKurir>
    }

    private fun kurir() {
        val client = ApiConfig.getApiService().listKurir()
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

    fun listBarang(): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        barang()
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    fun detailBarang(idDetailBarang: Int): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        barang(idDetailBarang, isDetailBarang = true)
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    fun addbarang(params: HashMap<String, String>): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        barang(params = params)
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    fun editBarang(
        idDetailBarang: Int,
        params: HashMap<String, String>,
    ): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        barang(idDetailBarang, params)
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    fun deleteBarang(idDetailBarang: Int): LiveData<ResponseDetailBarang> {
        _barang = MutableLiveData<ResponseDetailBarang>()
        barang(idDetailBarang)
        return _barang as MutableLiveData<ResponseDetailBarang>
    }

    private fun barang(
        idDetailBarang: Int? = null, params: HashMap<String, String>? = null,
        isDetailBarang: Boolean = false,
    ) {
        val client: Call<ResponseDetailBarang> = if (idDetailBarang == null) {
            if (params != null) {
                ApiConfig.getApiService().addBarang(params) // tambah data
            } else {
                ApiConfig.getApiService().listBarang() // list data
            }
        } else {
            if (params != null) {
                ApiConfig.getApiService().editBarang(idDetailBarang, params) // edit data
            } else {
                if (isDetailBarang) {
                    ApiConfig.getApiService().detailBarang(idDetailBarang) // detail data
                } else {
                    ApiConfig.getApiService().deleteBarang(idDetailBarang) // hapus data
                }
            }
        }

        client.enqueue(object : Callback<ResponseDetailBarang> {
            override fun onResponse(
                call: Call<ResponseDetailBarang>,
                response: Response<ResponseDetailBarang>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    _barang?.postValue(result!!)
                } else {
                    val errResult = response.errorBody()?.string()
                    val status = JSONObject(errResult!!).getInt("status")
                    val message = JSONObject(errResult).getString("message")
                    val responseDetailKurir = ResponseDetailBarang(message = message, status = status)
                    _barang?.postValue(responseDetailKurir)

                    Log.e(TAG, "onFailure: $responseDetailKurir")
                }
            }

            override fun onFailure(call: Call<ResponseDetailBarang>, t: Throwable) {
                _barang?.postValue(null)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


}