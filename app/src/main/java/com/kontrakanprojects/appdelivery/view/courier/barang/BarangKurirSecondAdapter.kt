package com.kontrakanprojects.appdelivery.view.courier.barang

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvCourierPackageListSecondBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir

class BarangKurirSecondAdapter(val activity: Activity):
    RecyclerView.Adapter<BarangKurirSecondAdapter.BarangKurirFirstAdapterViewHolder>() {

    private val listBarang = ArrayList<ResultsBarangKurir>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(barangs: List<ResultsBarangKurir>?) {
        if (barangs == null) return
        listBarang.clear()
        listBarang.addAll(barangs)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listBarang[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BarangKurirSecondAdapter.BarangKurirFirstAdapterViewHolder {
        val binding =
            RvCourierPackageListSecondBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangKurirFirstAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: BarangKurirSecondAdapter.OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: BarangKurirSecondAdapter.BarangKurirFirstAdapterViewHolder, position: Int) {
        holder.bind(listBarang[position])
    }

    override fun getItemCount() = listBarang.size

    inner class BarangKurirFirstAdapterViewHolder(private val binding: RvCourierPackageListSecondBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsBarangKurir: ResultsBarangKurir) {
            with(binding) {

                if (resultsBarangKurir.statusBarang == "4"){
                    var a = ""
                    a = activity.getString(R.string.rb_004)
                    tvStatusBarang01.text = a
                    tvNameCostumerRecycler.text = resultsBarangKurir.penerima
                }else{
                    //nothing
                }
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsBarangKurir) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsBarangKurir: ResultsBarangKurir)
    }
}