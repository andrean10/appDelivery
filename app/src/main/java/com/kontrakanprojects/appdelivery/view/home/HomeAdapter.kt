package com.kontrakanprojects.appdelivery.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.RvWaybillBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder>() {

    private val listTrackingBarang = ArrayList<ResultDetailBarang>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(trackingBarang: List<ResultDetailBarang>?) {
        if (trackingBarang == null) return
        listTrackingBarang.clear()
        listTrackingBarang.addAll(trackingBarang)
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
        holder.bind(listTrackingBarang[position])
    }

    override fun getItemCount() = listTrackingBarang.size

    inner class HomeAdapterViewHolder(private val binding: RvWaybillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultDetailBarang: ResultDetailBarang) {
            with(binding) {
                tvUpdated.text = resultDetailBarang.updatedAt
                tvDetailShipment.text = resultDetailBarang.statusBarang
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultDetailBarang) }
        }

    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultDetailBarang: ResultDetailBarang)
    }
}