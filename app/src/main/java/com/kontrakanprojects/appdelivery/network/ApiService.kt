package com.kontrakanprojects.appdelivery.network

import com.kontrakanprojects.appdelivery.model.auth.ResponseAuth
import com.kontrakanprojects.appdelivery.model.barang.ResponseDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResponseBarangKurir
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.model.profile.ResponseProfileDetail
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // login
    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap params: HashMap<String, String>): Call<ResponseAuth>

    // Tracking
    @GET("tracking/{kode_pelanggan}")
    fun tracking(@Path("kode_pelanggan") kodeResi: Int): Call<ResponseTrackings>

    // Admin
    @GET("admin/detail/{id_profile}")
    fun detailProfileAdmin(@Path("id_profile") idProfile: Int): Call<ResponseProfileDetail>

    @FormUrlEncoded
    @PATCH("admin/edit/{id_profile}")
    fun editProfileAdmin(
        @Path("id_profile") idProfile: Int,
        @FieldMap params: HashMap<String, String>,
    ): Call<ResponseProfileDetail>

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
    fun listBarang(): Call<ResponseDetailBarang>

    @GET("detail-barang/{id_barang}")
    fun detailBarang(@Path("id_barang") idDetailBarang: Int): Call<ResponseDetailBarang>

    @FormUrlEncoded
    @POST("detail-barang")
    fun addBarang(@FieldMap params: HashMap<String, String>): Call<ResponseDetailBarang>

    @FormUrlEncoded
    @PUT("detail-barang/{id}")
    fun editBarang(
        @Path("id") idDetailBarang: Int,
        @FieldMap params: HashMap<String, String>,
    ): Call<ResponseDetailBarang>

    @DELETE("detail-barang/{id}")
    fun deleteBarang(@Path("id") idDetailBarang: Int): Call<ResponseDetailBarang>

    //  Barang
    @GET("trackings")
    fun listTracking(): Call<ResponseTrackings>

    // dapatkan tracking berdasarkan id barang DESC
    @GET("trackings/{id_barang}")
    fun listTrackingbrg(@Path("id_barang") idBarang: Int): Call<ResponseTracking>

    @GET("kurir/tracking/{id_kurir}")
    fun listDataBarangAdmin(@Path("id_kurir") idKurir: Int): Call<ResponseBarangKurir>

    @Multipart
    @POST("tracking")
    fun addTracking(
        @PartMap params: HashMap<String, RequestBody>,
        @Part imagesParams: MultipartBody.Part?
    ): Call<ResponseTracking>
}
