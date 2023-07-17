package com.labbany.labbany.ui.addresses

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemAddressBinding
import com.labbany.labbany.pojo.response.AddressesResponse
import com.labbany.labbany.util.RecyclerViewOnClickListener
import kotlinx.coroutines.flow.MutableStateFlow

class AddressAdapter(
    val listener: RecyclerViewOnClickListener
) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    val sizeState = MutableStateFlow(-1)

    private var list = ArrayList<AddressesResponse.Address>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_address,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AddressesResponse.Address, position: Int) {

            binding.tvCity.text = data.city_arabic
            binding.tvDistrict.text = data.district_name
            binding.tvReceiverPhone.text = data.alter_contact
            binding.tvReceiverName.text = data.alter_name

            binding.root.setOnClickListener {
                    listener.onAddressClickListener(data)
            }

            binding.imgRemove.setOnClickListener {
                listener.onRemoveAddressClickListener(data, this, position)
            }

            binding.imgEdit.setOnClickListener {
                listener.onUpdateAddressClickListener(data)
            }

            binding.executePendingBindings()
        }


        fun visProgress(progressState: Boolean) {

            if (progressState) {
                binding.progressBar.visibility = View.VISIBLE
                binding.imgEdit.visibility = View.INVISIBLE
                binding.imgRemove.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.imgEdit.visibility = View.VISIBLE
                binding.imgRemove.visibility = View.VISIBLE
            }

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: ArrayList<AddressesResponse.Address>) {
        this.list = d
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAddress(mAddress: AddressesResponse.Address) {
        this.list.remove(mAddress)
        sizeState.value = this.list.size
        try {
            notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }

}