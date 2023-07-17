package com.labbany.labbany.ui.payment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemPaymentMethodBinding
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.util.RecyclerViewOnClickListener
import com.labbany.labbany.util.Utils
import com.squareup.picasso.Picasso

class PaymentAdapter(val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {


    private var list = ArrayList<VisaModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_payment_method,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: VisaModel, position: Int) {

            binding.imgRemove.visibility = View.VISIBLE

            binding.imgRemove.setOnClickListener {
                listener.onRemoveVisa(data, this, position)
            }

            if (!data.visa_type_img.isNullOrEmpty())
                Picasso.get().load(data.visa_type_img).into(binding.imgVisa)

            binding.tvNumber.text = Utils.displayVisa(data.card_number)

            binding.executePendingBindings()
        }

        fun visProgress(progressState: Boolean) {

            if (progressState) {
                binding.progressBar.visibility = View.VISIBLE
                binding.imgRemove.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.imgRemove.visibility = View.VISIBLE
            }

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeVisa( model:VisaModel) {
        this.list.remove(model)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: ArrayList<VisaModel>) {
        this.list = d
        notifyDataSetChanged()
    }

}