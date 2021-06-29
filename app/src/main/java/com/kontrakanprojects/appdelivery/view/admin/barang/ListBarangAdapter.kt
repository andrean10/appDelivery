package com.kontrakanprojects.appdelivery.view.admin.barang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.RvPackageListBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang

class ListBarangAdapter : RecyclerView.Adapter<ListBarangAdapter.ListBarangAdapterViewHolder>() {

    private val listBarang = ArrayList<ResultDetailBarang>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(barangs: List<ResultDetailBarang>?) {
        if (barangs == null) return
        listBarang.clear()
        listBarang.addAll(barangs)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listBarang[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListBarangAdapter.ListBarangAdapterViewHolder {
        val binding =
            RvPackageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListBarangAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: ListBarangAdapter.ListBarangAdapterViewHolder, position: Int) {
        holder.bind(listBarang[position])
    }

    override fun getItemCount() = listBarang.size

    inner class ListBarangAdapterViewHolder(private val binding: RvPackageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultDetailBarang: ResultDetailBarang) {
            with(binding) {
                tvNameCostumerRecycler.text = resultDetailBarang.penerima
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultDetailBarang) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultDetailBarang: ResultDetailBarang)
    }
}