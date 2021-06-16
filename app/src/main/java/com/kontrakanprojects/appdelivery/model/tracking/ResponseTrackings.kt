package com.kontrakanprojects.appdelivery.model.tracking

import com.google.gson.annotations.SerializedName

data class ResponseTrackings(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultsItem>? = null,

    @field:SerializedName("status")
    val status: Int? = null,
)

data class ResultsItem(

    @field:SerializedName("penerima")
    val penerima: String? = null,

    @field:SerializedName("kode_pelanggan")
    val kodePelanggan: Int? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id_kurir")
    val idKurir: Int? = null,

    @field:SerializedName("tracking")
    val tracking: List<TrackingItem>? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("detail_barang")
    val detailBarang: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id_barang")
    val idBarang: Int? = null,

    @field:SerializedName("status_barang")
    val statusBarang: String? = null,

    @field:SerializedName("nomor_hp")
    val nomorHp: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("distance")
    val distance: String? = null,
)

data class TrackingItem(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id_barang")
    val idBarang: Int? = null,

    @field:SerializedName("id_tracking")
    val idTracking: Int? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("detail")
    val detail: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,
)
