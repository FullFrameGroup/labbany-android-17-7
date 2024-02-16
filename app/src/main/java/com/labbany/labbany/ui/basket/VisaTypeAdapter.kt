package com.labbany.labbany.ui.basket

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemVisaBinding
import com.labbany.labbany.pojo.model.VisaTypeModel
import com.labbany.labbany.ui.basket.payment.OnCardBrandSelected
import com.labbany.labbany.util.Constants
import com.squareup.picasso.Picasso

class VisaTypeAdapter(val onCardBrandSelected: OnCardBrandSelected) :
    RecyclerView.Adapter<VisaTypeAdapter.ViewHolder>() {


    private var list = ArrayList<VisaTypeModel>()
    private var lastBinding: ItemVisaBinding? = null
    var selectedVisaType: VisaTypeModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_visa,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemVisaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: VisaTypeModel) {

            if (data.visa_type_id == Constants.Visas.VISA_TYPE_CASH_ID) {
                lastBinding = binding
                selectedVisaType = data

                binding.imgVisa.visibility = View.GONE
                binding.tvVisa.visibility = View.VISIBLE

                select(data)
            } else {

                binding.imgVisa.visibility = View.VISIBLE
                binding.tvVisa.visibility = View.GONE

//              //  Log.e(TAG, "bind: data.visa_img ${data.visa_type_img}")
                if (!data.visa_type_img.isNullOrEmpty())
                    Picasso.get().load(data.visa_type_img).into(binding.imgVisa)
            }

            binding.root.setOnClickListener {

                if (data.visa_type_id != Constants.Visas.VISA_TYPE_CASH_ID) {

                    lastBinding = binding
                    selectedVisaType = data

                    val isValidate = onCardBrandSelected.onCardBrandSelected(data)

                    if (isValidate)
                        select(data)

                } else {
                    select(data)
                }
            }

            binding.executePendingBindings()
        }

        private fun select(model: VisaTypeModel) {

            if (lastBinding != null)
                deSelect(lastBinding!!)

            lastBinding = binding
            selectedVisaType = model

            binding.root.background = ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.bg_solid_gray_corner_7sdp_border_red_1dp
            )
        }

        private fun deSelect(lastBinding: ItemVisaBinding) {

            lastBinding.root.background = ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.bg_solid_gray_corner_7sdp_border_gray_1dp
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCashType(mContext: Context) {
        this.list.add(
            VisaTypeModel(
                null,
                mContext.getString(R.string.cash),
                Constants.Visas.VISA_TYPE_CASH_ID
            )
        )
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: ArrayList<VisaTypeModel>) {
        val data = d.filter { it.visa_type_id != 2 }
        this.list.addAll(data)
        notifyDataSetChanged()
    }

    fun setCashSelected(mContext: Context) {
        selectedVisaType = VisaTypeModel(
            null,
            mContext.getString(R.string.cash),
            Constants.Visas.VISA_TYPE_CASH_ID
        )
    }

    fun getPaymentType(): String =
        when (selectedVisaType!!.visa_type_id) {
            Constants.Visas.VISA_TYPE_CASH_ID -> "CASH"
            Constants.Visas.MADA_ID -> "MADA"
            Constants.Visas.VISA_ID -> "VISA"
            else -> "VISA"
        }

    companion object {
        private val TAG = this::class.java.name
    }

}