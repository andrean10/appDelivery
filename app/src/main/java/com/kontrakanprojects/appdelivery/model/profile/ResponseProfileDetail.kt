package com.kontrakanprojects.appdelivery.model.profile

import com.google.gson.annotations.SerializedName

data class ResponseProfileDetail(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("results")
    val results: List<ResultsItem>? = null,

    @field:SerializedName("status")
    val status: Int? = null,
)

data class ResultsItem(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("foto_profil")
    val fotoProfil: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String? = null,

    @field:SerializedName("id_level_akses")
    val idLevelAkses: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id_kurir")
    val idKurir: Int? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,
)
