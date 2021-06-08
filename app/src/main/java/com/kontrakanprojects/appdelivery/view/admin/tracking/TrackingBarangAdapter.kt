package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.RvTrackingListBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.model.tracking.ResponseTrackings
import com.kontrakanprojects.appdelivery.model.tracking.ResultsItem
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.view.admin.couriers.ListCouriersAdapter

class TrackingBarangAdapter: RecyclerView.Adapter<TrackingBarangAdapter.TrackingBarangAdapterViewHolder>() {

    private val listItem = ArrayList<ResultsItem>()
    private val listTracking = ArrayList<ResultsItem>()
    private var onItemClickCallBack: TrackingBarangAdapter.OnItemClickCallBack? = null

    fun setData(tracking: List<ResultsItem>?) {
        if (tracking == null) return
        listItem.clear()
        listItem.addAll(tracking)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listItem[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackingBarangAdapter.TrackingBarangAdapterViewHolder {
        val binding =
            RvTrackingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackingBarangAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: TrackingBarangAdapter.OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: TrackingBarangAdapter.TrackingBarangAdapterViewHolder, position: Int) {
        holder.bind(listItem[position])
    }

    override fun getItemCount() = listItem.size

    inner class TrackingBarangAdapterViewHolder(private val binding: RvTrackingListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsItem: ResultsItem) {
            with(binding) {
//                Glide.with(itemView.context)
//                    .load(ApiConfig.URL + resultKurir.fotoProfil)
//                    .placeholder(R.drawable.no_profile_images)
//                    .error(R.drawable.no_profile_images)
//                    .into(circlePhotoProfile)

                tvNameCostumerRecycler.text = resultsItem.penerima
                tvStatusPackageRecycler.text = resultsItem.updatedAt
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsItem) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsItem: ResultsItem)
    }
}