package com.kontrakanprojects.appdelivery.model.kurir

data class ResponseKurir(
	val responseKurir: List<ResponseKurirItem?>? = null
)

data class ResponseKurirItem(
	val fotoProfil: String? = null,
	val updatedAt: String? = null,
	val namaLengkap: String? = null,
	val createdAt: String? = null,
	val idKurir: Int? = null,
	val username: String? = null,
	val alamat: String? = null
)

