package com.labbany.labbany.ui.basket

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemBasketBinding
import com.labbany.labbany.pojo.model.CartOrderModel
import com.labbany.labbany.pojo.request.MakeOrderRequest
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.labbany.labbany.util.Utils
import com.squareup.picasso.Picasso
import java.io.Serializable

class BasketAdapter(val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<BasketAdapter.ViewHolder>(),Serializable {


    private var list = ArrayList<CartOrderModel>()
//    private var mCoupon: OrderCouponResponse.Coupon? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_basket,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemBasketBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: CartOrderModel, position: Int) {

            val mContext = binding.root.context

            binding.tvProductName.text = data.product_name
            binding.tvProductPrice.text =
                "${data.total_price} ${mContext.getString(R.string.sar_2)}"

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

            binding.tvQuantity.text = "${data.qty}"

            setItemPrice(mContext, data)

            binding.imgPlus.setOnClickListener {

                binding.tvQuantity.text = "${++data.qty}"

                listener.onTotalPriceChanged(data.total_price, data.total_price + data.size_price!!)
                data.total_price += data.size_price

                setItemPrice(mContext, data)
            }

            binding.imgMinus.setOnClickListener {

                if (data.qty > 1) {
                    binding.tvQuantity.text = "${--data.qty}"

                    listener.onTotalPriceChanged(
                        data.total_price,
                        data.total_price - data.size_price!!
                    )

                    data.total_price -= data.size_price

                    setItemPrice(mContext, data)
                }
            }


            binding.imgRemove.setOnClickListener {
                listener.onDeleteCartItemClickListener(data, position)
            }

            binding.executePendingBindings()
        }

        @SuppressLint("SetTextI18n")
        private fun setItemPrice(mContext: Context, data: CartOrderModel) {

            Log.e(TAG, "setItemPrice: data.total_price before ${data.total_price}")
            binding.tvProductPrice.text =
                "${Utils.decimalFormat(data.total_price)} ${mContext.getString(R.string.sar_2)}"

/*
            if (mCoupon != null && !mCoupon!!.is_amount) {

                val percentage = mCoupon!!.percentage / 100

                Log.e(TAG, "setItemPrice: mCoupon!!.percentage ${mCoupon!!.percentage}")
               Log.e(TAG, "setItemPrice: percentage $percentage")

                data.total_price_with_discount = data.total_price - data.total_price * percentage

                Log.e(TAG, "setItemPrice: data.total_price_with_discount ${data.total_price_with_discount}")
                Log.e(TAG, "setItemPrice: data.total_price ${data.total_price}")

                binding.tvProductDiscount.visibility = View.VISIBLE

                binding.tvProductPrice.text = "${data.total_price_with_discount}"
                binding.tvProductDiscount.text = "${data.total_price}"

            } else {

                binding.tvProductPrice.text = "${data.total_price}"
            }
*/

        }

    }

    fun deleteItem(m: CartOrderModel, position: Int) {

        try {

            listener.onTotalPriceChanged(
                m.total_price,
                0.0
            )

            this.list.remove(m)
            notifyItemRemoved(position)

        } catch (e: Exception) {
        }

    }

    /*  fun applyCoupon(coupon: OrderCouponResponse.Coupon) {

          mCoupon = coupon

          if (!coupon.is_amount) {

              for (i in list.indices) {

                  list[i].total_price_with_discount = list[i].total_price * coupon.percentage

              }

              notifyItemRangeChanged(0, list.size)
          }

      }
  */
    fun submitData(d: ArrayList<CartOrderModel>) {
        this.list = d
        notifyDataSetChanged()
    }

    fun getBasket(): ArrayList<MakeOrderRequest.CartItem> {
        val data = ArrayList<MakeOrderRequest.CartItem>()

        this.list.forEach {
            data.add(
                MakeOrderRequest.CartItem(
                    it.cart_item_id,
                    it.qty,
                    it.total_price,
                    it.size_price!!
                )
            )
        }

        return data
    }


    fun isEmptyBasket(): Boolean = this.list.isNullOrEmpty()

    companion object {
        private val TAG = this::class.java.name
    }
}