package com.labbany.labbany.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemNotificationsBinding
import com.labbany.labbany.pojo.response.NotificationsResponse

class NotificationsAdapter :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {


    private var list = ArrayList<NotificationsResponse.Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_notifications,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NotificationsResponse.Notification) {

            binding.tvDate.text=data.notificationDate
            binding.tvDetails.text=data.notification

            binding.executePendingBindings()
        }

    }

    fun submitData(d: List<NotificationsResponse.Notification>) {
        this.list= d as ArrayList<NotificationsResponse.Notification>
        notifyDataSetChanged()
    }

    }