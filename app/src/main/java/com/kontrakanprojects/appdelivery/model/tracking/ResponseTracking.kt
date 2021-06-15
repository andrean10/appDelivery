package com.kontrakanprojects.appdelivery.model.tracking

import com.google.gson.annotations.SerializedName

data class ResponseTracking(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("results")
	val results: List<ResultTracking>? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ResultTracking(

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
	val longitude: String? = null
)
