package com.kontrakanprojects.appdelivery.view.courier.barang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvCourierPackageListFirstBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultsBarangKurir
import com.kontrakanprojects.appdelivery.view.admin.barang.ListBarangAdapter

class BarangKurirFirstAdapter(val activity: Activity, val context: Context):
    RecyclerView.Adapter<BarangKurirFirstAdapter.BarangKurirFirstAdapterViewHolder>() {

    private val listBarang = ArrayList<ResultsBarangKurir>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(barangs: List<ResultsBarangKurir>?) {
        if (barangs == null) return
        listBarang.clear()
        listBarang.addAll(barangs)
        notifyDataSetChanged()
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
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

                val idBarang = resultsBarangKurir.idBarang.toString()
                val statusBarang = resultsBarangKurir.statusBarang

                Log.d("anjir", "bind: $idBarang")

                relativeLayout02.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        val intent = Intent(context, ManageTrackingKurirActivity::class.java)
                        intent.putExtra("id_barang", idBarang)
                        intent.putExtra("status_barang", statusBarang)
                        context.startActivity(intent)
                    }
                })
            }
            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultsBarangKurir) }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultsBarangKurir: ResultsBarangKurir)
    }
}