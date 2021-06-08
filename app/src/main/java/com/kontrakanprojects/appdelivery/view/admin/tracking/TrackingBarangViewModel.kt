package com.kontrakanprojects.appdelivery.view.admin.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kontrakanprojects.appdelivery.model.kurir.ResponseKurir
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTracking
import com.kontrakanprojects.appdelivery.view.admin.couriers.CouriersViewModel

class TrackingBarangViewModel : ViewModel() {

    private var _tracking: MutableLiveData<ResponseTracking>? = null

    private val TAG = TrackingBarangViewModel::class.simpleName

    

}