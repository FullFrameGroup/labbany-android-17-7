package com.labbany.labbany.ui.order_details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemOrderDetailsCartBinding
import com.labbany.labbany.pojo.model.Order

class ItemsAdapter :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {


    private var list = ArrayList<Order.CartItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_order_details_cart,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemOrderDetailsCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: Order.CartItem, position: Int) {

            val mContext = binding.root.context

            binding.tvPosition.text = "${position + 1}"
            binding.tvProductCount.text = "x ${data.qty}"
            binding.tvProductName.text = data.product_name
            binding.tvProductPrice.text = "${data.total_price}"

            var details = ""

            if (data.cut_id != null)
                details += "${mContext.getString(R.string.chopping)} (${data.cut_name}) - "

            if (data.packing_id != null)
                details += "${mContext.getString(R.string.encapsulation)} (${data.packing_name}) - "

            if (data.extra_id != null)
                details += "${mContext.getString(R.string.minced_meat)} (${data.extra_name}) - "

            if (data.size_id != null)
                details += "${mContext.getString(R.string.size)} (${data.size_name}) - "

            binding.tvProductVolume.text = details

            //is last item
            if (position == list.size - 1)
                binding.hView.visibility = View.INVISIBLE

            binding.executePendingBindings()
        }

    }

    fun submitData(d: Order.CartItem) {
        this.list.add(d)
        notifyDataSetChanged()
    }

}