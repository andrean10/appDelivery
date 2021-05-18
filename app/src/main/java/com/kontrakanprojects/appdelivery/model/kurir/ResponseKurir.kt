package com.kontrakanprojects.appdelivery.model.kurir

import com.google.gson.annotations.SerializedName

data class ResponseKurir(

	@field:SerializedName("ResponseKurir")
	val responseKurir: List<ResponseKurirItem?>? = null,
)

data class ResponseKurirItem(

	@field:SerializedName("foto_profil")
	val fotoProfil: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_kurir")
	val idKurir: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,
)
