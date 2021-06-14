package com.kontrakanprojects.appdelivery.model.admin

import com.google.gson.annotations.SerializedName

data class ResponseAdmin(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("results")
	val results: List<ResultsAdmin>? = null,

	@field:SerializedName("status")
	val status: Int? = null,
)

data class ResultsAdmin(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("id_level_akses")
	val idLevelAkses: Int? = null,

	@field:SerializedName("id_login")
	val idLogin: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,
)
