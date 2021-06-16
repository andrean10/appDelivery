package com.kontrakanprojects.appdelivery.view.admin.couriers

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.RvCourierListBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig

class ListCouriersAdapter :
    RecyclerView.Adapter<ListCouriersAdapter.ListCouriersAdapterViewHolder>() {

    private val listCourier = ArrayList<ResultKurir>()
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setData(couriers: List<ResultKurir>?) {
        if (couriers == null) return
        listCourier.clear()
        listCourier.addAll(couriers)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = listCourier[position]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListCouriersAdapterViewHolder {
        val binding =
            RvCourierListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCouriersAdapterViewHolder(binding)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    override fun onBindViewHolder(holder: ListCouriersAdapterViewHolder, position: Int) {
        holder.bind(listCourier[position])
    }

    override fun getItemCount() = listCourier.size

    inner class ListCouriersAdapterViewHolder(private val binding: RvCourierListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultKurir: ResultKurir) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(ApiConfig.IMG_URL + resultKurir.fotoProfil)
                    .listener(listener(this))
                    .placeholder(R.drawable.no_profile_images)
                    .error(R.drawable.no_profile_images)
                    .into(circlePhotoProfile)

                nameCourier.text = resultKurir.namaLengkap
            }

            itemView.setOnClickListener { onItemClickCallBack?.onItemClicked(resultKurir) }
        }
    }

    private fun listener(binding: RvCourierListBinding) = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBar.visibility = View.GONE
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBar.visibility = View.GONE
            return false
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(resultKurir: ResultKurir)
    }
}