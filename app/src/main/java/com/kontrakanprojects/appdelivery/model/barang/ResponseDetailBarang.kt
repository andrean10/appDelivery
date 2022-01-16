package com.kontrakanprojects.appdelivery.model.barang

import com.google.gson.annotations.SerializedName

data class ResponseDetailBarang(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("results")
	val results: List<ResultDetailBarang>? = null,

	@field:SerializedName("status")
	val status: Int? = null,
)

data class ResultDetailBarang(

    @field:SerializedName("detail_barang")
    val detailBarang: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id_barang")
    val idBarang: Int? = null,

    @field:SerializedName("penerima")
    val penerima: String? = null,

    @field:SerializedName("kode_pelanggan")
    val kodePelanggan: Int? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("status_barang")
    val statusBarang: String? = null,

    @field:SerializedName("id_kurir")
    val idKurir: Int? = null,

    @field:SerializedName("nomor_hp")
    val nomorHp: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("foto_diterima")
    val fotoDiterima: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("distance")
    val distance: String? = null,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String? = null,

    @field:SerializedName("estiminasi")
    val estiminasi: String? = null,
)
