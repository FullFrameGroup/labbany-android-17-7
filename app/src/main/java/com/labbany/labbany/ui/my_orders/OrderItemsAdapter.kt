package com.labbany.labbany.ui.my_orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemOrderCartBinding
import com.labbany.labbany.pojo.model.Order
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.labbany.labbany.util.Utils
import com.squareup.picasso.Picasso

class OrderItemsAdapter(private val listener: RecyclerViewOnClickListener, private val orderId: Int) :
    RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {


    private var list = ArrayList<Order.CartItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_order_cart,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemOrderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: Order.CartItem) {

            val mContext = binding.root.context

            binding.tvProductName.text = data.product_name

            if (data.price_after_discount == null || data.price_after_discount == 0) {
                binding.tvProductPrice.text =
                    "${Utils.decimalFormat(data.total_price)} ${mContext.getString(R.string.sar_2)}"
            }else{
                binding.tvProductDiscount.visibility=View.VISIBLE

                binding.tvProductPrice.text =
                    "${Utils.decimalFormat(data.price_after_discount)} ${mContext.getString(R.string.sar_2)}"
                binding.tvProductDiscount.text =
                    "${Utils.decimalFormat(data.total_price)} ${mContext.getString(R.string.sar_2)}"

            }

            if (!data.product_image.isNullOrEmpty())
                Picasso.get().load(data.product_image).into(binding.imgProduct)

            var details = ""

            if (data.cut_id != null)
                details += "${mContext.getString(R.string.chopping)} (${data.cut_name}) - "

            if (data.packing_id != null)
                details += "${mContext.getString(R.string.encapsulation)} (${data.packing_name}) - "

            if (data.extra_id != null)
                details += "${mContext.getString(R.string.minced_meat)} (${data.extra_name}) - "

            if (data.size_id != null)
                details += "${mContext.getString(R.string.size)} (${data.size_name}) - "

            binding.tvProductDetails.text = details

            binding.tvCount.text = "x${data.qty}"

            binding.root.setOnClickListener {
                listener.onOrderClickListener(orderId )
            }

            binding.executePendingBindings()
        }

    }

    fun submitData(d: List<Order.CartItem>) {
        this.list = d as ArrayList<Order.CartItem>
        notifyDataSetChanged()
    }

}