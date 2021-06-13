package com.kontrakanprojects.appdelivery.model.auth

import com.google.gson.annotations.SerializedName

data class ResponseAuth(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,
)

data class Result(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("id_level_akses")
	val idLevelAkses: Int? = null,

	@field:SerializedName("id_login")
	val idLogin: Int? = null,

	@field:SerializedName("id_kurir")
	val idKurir: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,
)
