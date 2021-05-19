package com.kontrakanprojects.appdelivery.model.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Result(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("id_level_akses")
	val idLevelAkses: Int? = null,

	@field:SerializedName("id_login")
	val idLogin: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)
