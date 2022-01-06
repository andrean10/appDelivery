package com.kontrakanprojects.appdelivery.view.courier.barang.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvCourierPackageListFirstBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir

class BarangKurirFirstAdapter(val activity: Activity) :
    RecyclerView.Adapter<BarangKurirFirstAdapter.BarangKurirFirstAdapterViewHolder>() {

    private val listBarang = ArrayList<ResultsBarangKurir>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(barangs: List<ResultsBarangKurir>?) {
        if (barangs == null) return
        listBarang.clear()
        listBarang.addAll(barangs)
        notifyDataSetChanged()
    }

    fun clearData() {
        listBarang.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BarangKurirFirstAdapterViewHolder {
        val binding =
            RvCourierPackageListFirstBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return BarangKurirFirstAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BarangKurirFirstAdapterViewHolder,
        position: Int,
    ) {
        holder.bind(listBarang[position])
    }

    override fun getItemCount() = listBarang.size

    inner class BarangKurirFirstAdapterViewHolder(private val binding: RvCourierPackageListFirstBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultsBarangKurir: ResultsBarangKurir) {
            with(binding) {

                var a = ""
                when (resultsBarangKurir.statusBarang) {
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
                    "5" -> {
                        a = activity.getString(R.string.rb_005)
                    }
                }

                tvStatusBarang01.text = a
                tvLocation.text = activity.getString(R.string.kmm, resultsBarangKurir.distance)
                tvNameCostumerRecycler.text = resultsBarangKurir.penerima
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsBarangKurir) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsBarangKurir: ResultsBarangKurir)
    }
}