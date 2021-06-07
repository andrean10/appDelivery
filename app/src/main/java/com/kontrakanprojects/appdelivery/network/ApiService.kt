package com.kontrakanprojects.appdelivery.network

import com.kontakanprojects.apptkslb.model.ResponseAuth
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // login
    @POST("login")
    fun login(@FieldMap params: HashMap<String, Any>): Call<ResponseAuth>

    // Tracking
    @GET("tracking")
    fun tracking(@Query("kode_pelanggan") kodeResi: Int): Call<ResponseTracking>

    // Kurir
    @GET("kurir")
    fun listKurir(): Call<ResponseKurir>

    @GET("kurir/{id}")
    fun detailKurir(@Path("id") idKurir: Int): Call<ResponseKurir>

    @FormUrlEncoded
    @POST("kurir")
    fun addKurir(@FieldMap dataKurir: Map<String, String>): Call<ResponseKurir>

    @FormUrlEncoded
    @PATCH("kurir/{id}")
    fun editKurir(
        @Path("id") idKurir: Int,
        @FieldMap dataKurir: Map<String, String>,
    ): Call<ResponseKurir>

    @DELETE("kurir/{id}")
    fun deleteKurir(@Path("id") idKurir: Int): Call<ResponseKurir>

    // Barang
    @GET("detail-barang")
    fun listDetailBarang(): Call<ResponseDetailBarang>

    @GET("detail-barang/{id_barang}")
    fun detailBarang(@Path("id_barang") idDetailBarang: Int): Call<ResponseDetailBarang>
}
