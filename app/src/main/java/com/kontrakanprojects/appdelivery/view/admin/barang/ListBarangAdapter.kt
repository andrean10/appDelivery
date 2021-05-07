package com.kontrakanprojects.appdelivery.view.admin.barang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.ItemsBarangBinding

class ListBarangAdapter : RecyclerView.Adapter<ListBarangAdapter.ListBarangAdapterViewHolder>() {

    //    private val listKelas = ArrayList<ResultsKelas>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

//    fun setData(kelas: List<ResultsKelas>?) {
//        if (kelas == null) return
//        listKelas.clear()
//        listKelas.addAll(kelas)
//        notifyDataSetChanged()
//    }
//
//    fun getData(position: Int) = listKelas[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBarangAdapterViewHolder {
        val binding =
            ItemsBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListBarangAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: ListBarangAdapterViewHolder, position: Int) {
//        holder.bind(listKelas[position])
    }

    override fun getItemCount() = 0 // listKelas.size

    inner class ListBarangAdapterViewHolder(private val binding: ItemsBarangBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        fun bind(resultsKelas: ResultsKelas) {
//            with(binding) {
//                Glide.with(itemView.context)
//                    .load(resultsKelas.)
//            }

//            binding.tvNameCouriers.text = resultsKelas.namaKelas

//            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsKelas) }
//        }
    }

    interface OnItemClickCallBack {
//        fun onItemClicked(resultsKelas: ResultsKelas)
    }
}