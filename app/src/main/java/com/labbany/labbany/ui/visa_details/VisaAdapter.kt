package com.labbany.labbany.ui.visa_details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemPaymentMethodBinding
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.ui.basket.payment.OnVisaSelected
import com.labbany.labbany.util.Utils

class VisaAdapter(val listener: OnVisaSelected) :
    RecyclerView.Adapter<VisaAdapter.ViewHolder>() {


    private var list = ArrayList<VisaModel>()
    private var visaTypeImage: Bitmap? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_payment_method,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: VisaModel) {

            binding.imgRemove.visibility = View.GONE

            binding.root.setOnClickListener {
                listener.onVisaSelected(data)
            }

            if (visaTypeImage != null)
                binding.imgVisa.setImageBitmap(visaTypeImage)

            binding.tvNumber.text = Utils.displayVisa(data.card_number)

            binding.executePendingBindings()
        }

    }

    fun setVisaTypeImage(bitmap: Bitmap?) {
        visaTypeImage = bitmap
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: ArrayList<VisaModel>) {
        this.list = d
        notifyDataSetChanged()
    }

}