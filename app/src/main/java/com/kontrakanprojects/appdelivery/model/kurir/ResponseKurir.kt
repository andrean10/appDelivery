package com.kontrakanprojects.appdelivery.model.kurir

import com.google.gson.annotations.SerializedName

data class ResponseKurir(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("results")
    val results: List<ResultKurir>? = null,

    @field:SerializedName("status")
    val status: Int? = null,
)

data class ResultKurir(

    @field:SerializedName("foto_profil")
    var fotoProfil: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id_kurir")
    val idKurir: Int? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,
) {
    override fun toString(): String {
        return namaLengkap ?: "NULL"
    }
}
