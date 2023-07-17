package com.labbany.labbany.ui.my_orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemMyOrdersBinding
import com.labbany.labbany.pojo.model.Order
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.labbany.labbany.util.Utils

class OrdersAdapter(private val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {


    private var list = ArrayList<Order>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_my_orders,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemMyOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: Order, position: Int) {

            val mContext = binding.root.context

            binding.tvState.text = data.order_status_str
            binding.tvOrderNumber.text = "#${data.o_id}"
            binding.tvTotal.text =
                "${Utils.decimalFormat(data.total)} ${mContext.getString(R.string.sar_2)}"

            val orderItemsAdapter = OrderItemsAdapter(listener,data.o_id)

            binding.rvItems.adapter = orderItemsAdapter

            orderItemsAdapter.submitData(data.cart_item)

            binding.imgRemove.setOnClickListener {
                listener.onCancelOrderClickListener(data, position)
            }

            binding.root.setOnClickListener {
                listener.onOrderClickListener(data.o_id)
            }

            binding.executePendingBindings()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: List<Order>) {
        this.list = d as ArrayList<Order>
        notifyDataSetChanged()
    }

    fun deleteItem(m: Order, position: Int) {

        try {
            this.list.remove(m)
            notifyItemRemoved(position)

        } catch (e: Exception) {
        }

    }

}