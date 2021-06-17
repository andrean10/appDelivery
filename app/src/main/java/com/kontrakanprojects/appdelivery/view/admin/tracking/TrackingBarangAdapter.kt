package com.kontrakanprojects.appdelivery.view.admin.tracking

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvTrackingListBinding
import com.kontrakanprojects.appdelivery.model.tracking.ResultsItem

class TrackingBarangAdapter(val activity:Activity): RecyclerView.Adapter<TrackingBarangAdapter.TrackingBarangAdapterViewHolder>() {

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
                tvNameCostumerRecycler.text = resultsItem.penerima
                val z = resultsItem.kodePelanggan.toString()
                var a = ""
                when (resultsItem.statusBarang) {
                    "1" -> {
                        a = activity.getString(R.string.rb_001)
                    }
                    "2" -> {
                        a = activity.getString(R.string.rb_002)
                    }
                    "3" -> {
                        a = activity.getString(R.string.rb_003)
                    }
                    "4" -> {
                        a = activity.getString(R.string.rb_004)
                    }
                }

                tvStatusBarang.text = "Nomor Resi : $z"
                tvDistance.text = activity.getString(R.string.kmm, resultsItem.distance)
                tvStatusPackageRecycler.text = "[${resultsItem.createdAt}] $a"
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsItem) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsItem: ResultsItem)
    }
}