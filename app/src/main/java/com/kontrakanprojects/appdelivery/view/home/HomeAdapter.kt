package com.kontrakanprojects.appdelivery.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.RvWaybillBinding
import com.kontrakanprojects.appdelivery.model.tracking.TrackingItem

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder>() {

    private val listTracking = ArrayList<TrackingItem>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(trackingBarang: List<TrackingItem>?) {
        if (trackingBarang == null) return
        listTracking.clear()
        listTracking.addAll(trackingBarang)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapterViewHolder {
        val binding =
            RvWaybillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: HomeAdapterViewHolder, position: Int) {
        holder.bind(listTracking[position])
    }

    override fun getItemCount() = listTracking.size

    inner class HomeAdapterViewHolder(private val binding: RvWaybillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trackingItem: TrackingItem) {
            with(binding) {
                tvUpdated.text = trackingItem.updatedAt
                tvDetailShipment.text = trackingItem.detail
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(trackingItem) }
        }

    }

    interface OnItemClickCallBack {
        fun onItemClicked(trackingItem: TrackingItem)
    }
}