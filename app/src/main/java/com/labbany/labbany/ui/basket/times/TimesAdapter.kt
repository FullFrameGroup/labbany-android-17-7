package com.labbany.labbany.ui.basket.times

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemTimeBinding
import com.labbany.labbany.pojo.response.TimeSlotResponse
import com.labbany.labbany.util.RecyclerViewOnClickListener

class TimesAdapter(val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<TimesAdapter.ViewHolder>() {


    private var list = ArrayList<TimeSlotResponse.TimeSlot>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_time,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemTimeBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: TimeSlotResponse.TimeSlot) {

            val mContext = binding.root.context

            binding.tvYes.text =
                "${mContext.getString(R.string.from)} ${data.from_time} ${mContext.getString(R.string.to)} ${data.to_time}"

            binding.root.setOnClickListener { listener.onRootClickListener(binding, data) }

            binding.executePendingBindings()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: List<TimeSlotResponse.TimeSlot>) {
        this.list = d as ArrayList<TimeSlotResponse.TimeSlot>
        notifyDataSetChanged()
    }


    companion object {
//        private val TAG = this::class.java.name
    }
}