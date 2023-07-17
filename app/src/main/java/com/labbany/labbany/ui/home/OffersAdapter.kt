package com.labbany.labbany.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemOfferBinding
import com.labbany.labbany.pojo.model.Banner
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class OffersAdapter(val listener: RecyclerViewOnClickListener) :
    SliderViewAdapter<OffersAdapter.ViewHolder>() {


    private var list = ArrayList<Banner>()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_offer,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getCount(): Int = list.size

    inner class ViewHolder(val binding: ItemOfferBinding) : SliderViewAdapter.ViewHolder(binding.root) {

        fun bind(data: Banner) {

            if (!data.bannerimage.isNullOrEmpty())
                Picasso.get().load(data.bannerimage).into(binding.img)

            binding.root.setOnClickListener {
                listener.onOfferClickListener(binding,data)
            }

            binding.executePendingBindings()
        }

    }

    fun submitData(d: List<Banner>) {
        this.list= d as ArrayList<Banner>
        notifyDataSetChanged()
    }

    }