package com.kontrakanprojects.appdelivery.view.courier.barang

import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking

interface ItemClickListener {
    fun onItemClick(data: ResultsBarangKurir)
}