package com.kontrakanprojects.appdelivery.network

import com.kontrakanprojects.appdelivery.model.auth.ResponseAuth
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.model.profile.ResponseProfileDetail
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // login
    @FormUrlEncoded
    @POST("login")
    fun loginAdmin(@FieldMap params: HashMap<String, String>): Call<ResponseAuth>

    @FormUrlEncoded
    @POST("kurir/login")
    fun loginKurir(@FieldMap params: HashMap<String, String>): Call<ResponseAuth>

    // Tracking
    @GET("tracking/{kode_pelanggan}")
    fun tracking(@Path("kode_pelanggan") kodeResi: Int): Call<ResponseTrackings>

    // Admin
    @GET("admin/{id_profile}")
    fun detailProfileAdmin(@Path("id_profile") idProfile: Int): Call<ResponseProfileDetail>

    // Kurir
    @GET("kurir")
    fun listKurir(): Call<ResponseKurir>

    @GET("kurir/{id}")
    fun detailKurir(@Path("id") idKurir: Int): Call<ResponseKurir>

    @GET("kurir/{id_profile}")
    fun detailProfileKurir(@Path("id_profile") idProfile: Int): Call<ResponseProfileDetail>

    @Multipart
    @POST("kurir")
    fun addKurir(
        @PartMap params: HashMap<String, RequestBody>,
        @Part imagesParams: MultipartBody.Part?,
    ): Call<ResponseKurir>

    @FormUrlEncoded
    @PATCH("kurir/{id}")
    fun editKurir(
        @Path("id") idKurir: Int,
        @FieldMap params: HashMap<String, String>,
    ): Call<ResponseKurir>

    @Multipart
    @POST("kurir/gantifoto/{id}")
    fun editPhotoProfil(
        @Path("id") idKurir: Int,
        @Part imagesParams: MultipartBody.Part?,
    ): Call<ResponseKurir>

    @PATCH("kurir/hapusfoto/{id}")
    fun deletePhotoProfil(@Path("id") idKurir: Int): Call<ResponseKurir>

    @DELETE("kurir/{id}")
    fun deleteKurir(@Path("id") idKurir: Int): Call<ResponseKurir>

    // Barang
    @GET("detail-barang")
    fun listDetailBarang(): Call<ResponseDetailBarang>

    @GET("detail-barang/{id_barang}")
    fun detailBarang(@Path("id_barang") idDetailBarang: Int): Call<ResponseDetailBarang>

    //  Barang
    @GET("trackings")
    fun listTracking(): Call<ResponseTrackings>
}
