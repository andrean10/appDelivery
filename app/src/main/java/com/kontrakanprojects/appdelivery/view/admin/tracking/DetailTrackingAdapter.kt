package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.RvWaybillBinding
import com.kontrakanprojects.appdelivery.model.tracking.ResultTracking
import kotlin.collections.ArrayList

class DetailTrackingAdapter: RecyclerView.Adapter<DetailTrackingAdapter.DetailTrackingAdapterViewHolder>() {

    private val listTracking = ArrayList<ResultTracking>()
    private var onItemClickCallBack: DetailTrackingAdapter.OnItemClickCallBack? = null

    fun setData(tracking: List<ResultTracking>?) {
        if (tracking == null) return
        listTracking.clear()
        listTracking.addAll(tracking)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listTracking[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DetailTrackingAdapter.DetailTrackingAdapterViewHolder {
        val binding =
            RvWaybillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailTrackingAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: DetailTrackingAdapter.OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: DetailTrackingAdapter.DetailTrackingAdapterViewHolder, position: Int) {
        holder.bind(listTracking[position])
    }

    override fun getItemCount() = listTracking.size

    inner class DetailTrackingAdapterViewHolder(private val binding: RvWaybillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultTracking: ResultTracking) {
            with(binding) {

                tvUpdated.text = resultTracking.createdAt
                tvDetailShipment.text = resultTracking.detail
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultTracking) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultTracking: ResultTracking)
    }
}