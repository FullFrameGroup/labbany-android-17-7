package com.labbany.labbany.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemHomeBodyBinding
import com.labbany.labbany.pojo.model.ProductModel
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.squareup.picasso.Picasso

class HomeBodyAdapter(val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<HomeBodyAdapter.ViewHolder>() {


    private var list = ArrayList<ProductModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_home_body,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemHomeBodyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) {

//            binding.tvName.text = product.productName

            Log.e(TAG, "bind: productId ${product.productId}")

            if (!product.productImage.isNullOrEmpty())
                Picasso.get().load(product.productImage).into(binding.img)

            binding.img.setOnClickListener {
                listener.onRootClickListener(binding, product)
            }

            binding.executePendingBindings()
        }

    }

    fun submitData(d: ArrayList<ProductModel>) {
        this.list = d
        notifyDataSetChanged()
    }


    fun clear(){
        this.list.clear()
        notifyDataSetChanged()
    }

    companion object{
        private val TAG=this::class.java.name
    }

}