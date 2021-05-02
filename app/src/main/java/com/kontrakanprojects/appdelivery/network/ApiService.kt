package com.kontrakanprojects.appdelivery.network

import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // login
//    @POST("login")
//    fun login(): Call<>

    // Kurir
    @GET("kurir")
    fun showKurir(): Call<ResponseKurir>

    @FormUrlEncoded
    @POST("kurir")
    fun addKurir(@FieldMap dataKurir: Map<String, String>): Call<ResponseKurir>

    @FormUrlEncoded
    @PATCH("kurir/{id}")
    fun editKurir(
        @Path("id") idKurir: Int,
        @FieldMap dataKurir: Map<String, String>
    ): Call<ResponseKurir>

    @DELETE("kurir/{id}")
    fun deleteKurir(@Path("id") idKurir: Int): Call<ResponseKurir>

    // Barang
//    @GET("detail-barang")
//    fun showDetailBarang(): Call<>
}
