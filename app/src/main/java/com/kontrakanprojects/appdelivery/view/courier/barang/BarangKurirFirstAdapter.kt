package com.kontrakanprojects.appdelivery.view.courier.barang

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvCourierPackageListFirstBinding
import com.kontrakanprojects.appdelivery.databinding.RvPackageListBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.view.admin.barang.ListBarangAdapter

class BarangKurirFirstAdapter(val activity: Activity):
    RecyclerView.Adapter<BarangKurirFirstAdapter.BarangKurirFirstAdapterViewHolder>() {

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
    ): BarangKurirFirstAdapter.BarangKurirFirstAdapterViewHolder {
        val binding =
            RvCourierPackageListFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangKurirFirstAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: BarangKurirFirstAdapter.OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: BarangKurirFirstAdapter.BarangKurirFirstAdapterViewHolder, position: Int) {
        holder.bind(listBarang[position])
    }

    override fun getItemCount() = listBarang.size

    inner class BarangKurirFirstAdapterViewHolder(private val binding: RvCourierPackageListFirstBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultsBarangKurir: ResultsBarangKurir) {
            with(binding) {

                if (resultsBarangKurir.statusBarang == "1"){
                    var a = ""
                    a = activity.getString(R.string.rb_001)
                    tvStatusBarang01.setText(a)

                    location.text = resultsBarangKurir.distance
                    tvNameCostumerRecycler.text = resultsBarangKurir.penerima
                }else if (resultsBarangKurir.statusBarang == "2"){
                    var a = ""
                    a = activity.getString(R.string.rb_002)
                    tvStatusBarang01.setText(a)

                    location.text = resultsBarangKurir.distance
                    tvNameCostumerRecycler.text = resultsBarangKurir.penerima
                }else if (resultsBarangKurir.statusBarang == "3"){
                    var a = ""
                    a = activity.getString(R.string.rb_003)
                    tvStatusBarang01.setText(a)

                    location.text = resultsBarangKurir.distance
                    tvNameCostumerRecycler.text = resultsBarangKurir.penerima
                }

                Log.d("testing1", "bind: $resultsBarangKurir")

            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsBarangKurir) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsBarangKurir: ResultsBarangKurir)
    }
}