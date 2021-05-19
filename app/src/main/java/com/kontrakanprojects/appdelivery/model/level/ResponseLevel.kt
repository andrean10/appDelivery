package com.kontrakanprojects.appdelivery.model.level

import com.google.gson.annotations.SerializedName

data class ResponseLevel(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ResultsItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("id_level_akses")
	val idLevelAkses: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("tipe_aktor")
	val tipeAktor: String? = null
)
