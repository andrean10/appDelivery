package com.kontrakanprojects.appdelivery.network

import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.model.level.ResponseLevel
import com.kontrakanprojects.appdelivery.model.login.ResponseLogin
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Login
    @POST("login")
    fun login(): Call<ResponseLogin>


    // Kurir
    @GET("kurir")
    fun showKurir(): Call<ResponseKurir>

    @FormUrlEncoded
    @POST("kurir")
    fun addKurir(@FieldMap dataKurir: Map<String, String>): Call<ResponseKurir>

    @FormUrlEncoded
    @PATCH("kurir/{id_kurir}")
    fun editKurir(
        @Path("id") idKurir: Int,
        @FieldMap dataKurir: Map<String, String>
    ): Call<ResponseKurir>

    @DELETE("kurir/{id_kurir}")
    fun deleteKurir(@Path("id") idKurir: Int): Call<ResponseKurir>


    // Barang
    @GET("detail-barang")
    fun showDetailBarang(): Call<ResponseDetailBarang>

    @FormUrlEncoded
    @POST("detail-barang")
    fun addDetailBarang(@FieldMap dataDetailBarang: Map<String, String>): Call<ResponseDetailBarang>


    // Tracking
    @GET("tracking")
    fun showTracking(): Call<ResponseTracking>

    @FormUrlEncoded
    @POST("kurir")
    fun addTracking(@FieldMap dataTracking: Map<String, String>): Call<ResponseTracking>


    // level
    @GET("level-akses")
    fun showLevelAkses(): Call<ResponseLevel>

    @FormUrlEncoded
    @POST("kurir")
    fun addLevelAkses(@FieldMap dataLevel: Map<String, String>): Call<ResponseLevel>
}
