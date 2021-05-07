package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.databinding.ItemsCouriersBinding

class ListCouriersAdapter :
    RecyclerView.Adapter<ListCouriersAdapter.ListCouriersAdapterViewHolder>() {

    //    private val listKelas = ArrayList<ResultsKelas>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

//    fun setData(kelas: List<ResultsKelas>?) {
//        if (kelas == null) return
//        listKelas.clear()
//        listKelas.addAll(kelas)
//        notifyDataSetChanged()
//    }

//    fun getData(position: Int) = listKelas[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListCouriersAdapterViewHolder {
        val binding =
            ItemsCouriersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCouriersAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: ListCouriersAdapterViewHolder, position: Int) {
//        holder.bind(listKelas[position])
    }

    override fun getItemCount() = 0 // listKelas.size

    inner class ListCouriersAdapterViewHolder(private val binding: ItemsCouriersBinding) :
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