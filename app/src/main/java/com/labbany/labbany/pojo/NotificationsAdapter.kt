/*
package com.ladybug.ladybug.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ladybug.ladybug.R
import com.ladybug.ladybug.databinding.ItemNotificationOffBinding
import com.ladybug.ladybug.databinding.ItemNotificationOnBinding
import com.ladybug.ladybug.pojo.response.NotificationsResponse
import com.ladybug.ladybug.util.Constants
import com.ladybug.ladybug.util.RecyclerViewOnClickListener

class NotificationsAdapter(private val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = ArrayList<NotificationsResponse.Notification>()

    override fun getItemViewType(position: Int): Int =
        if (list[position].is_read) Constants.NOTIFICATIONS_OFF else Constants.NOTIFICATIONS_ON


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =

        if (viewType == Constants.NOTIFICATIONS_ON)
            OnViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_notification_on,
                    parent,
                    false
                )
            )
        else
            OffViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_notification_off,
                    parent,
                    false
                )
            )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is OnViewHolder)
            holder.bindOn(list[position])
        else if (holder is OffViewHolder)
            holder.bindOff(list[position])

    }

    override fun getItemCount(): Int = list.size

    inner class OnViewHolder(val binding: ItemNotificationOnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOn(notification: NotificationsResponse.Notification) {

            if (notification.data.url != null)
                binding.tvAction.visibility = View.VISIBLE
            else
                binding.tvAction.visibility = View.GONE

            binding.tvTitle.text = notification.title
            binding.tvBody.text = notification.body
            binding.tvTime.text = notification.created_at

            if (notification.type == Constants.NotificationsTypes.FarmInvitation)
                binding.tvAction.setOnClickListener {
                    listener.acceptFarmInvitationListener(
                        Constants.NOTIFICATIONS_ON,
                        this,
                        notification.data
                    )
                }

            binding.executePendingBindings()
        }

         fun visProgress(b: Boolean) {

            if (b) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvAction.visibility = View.INVISIBLE
            } else {
                binding.tvAction.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }

        }


    }

    inner class OffViewHolder(val binding: ItemNotificationOffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOff(notification: NotificationsResponse.Notification) {

            if (notification.data.url != null)
                binding.tvAction.visibility = View.VISIBLE
            else
                binding.tvAction.visibility = View.GONE

            binding.tvTitle.text = notification.title
            binding.tvBody.text = notification.body
            binding.tvTime.text = notification.created_at

            if (notification.type == Constants.NotificationsTypes.FarmInvitation)
                binding.tvAction.setOnClickListener {
                    listener.acceptFarmInvitationListener(
                        Constants.NOTIFICATIONS_OFF,
                        this,
                        notification.data
                    )
                }


            binding.executePendingBindings()
        }

         fun visProgress(b: Boolean) {

            if (b) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvAction.visibility = View.INVISIBLE
            } else {
                binding.tvAction.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }

        }

    }


    fun submitData(d: ArrayList<NotificationsResponse.Notification>) {
        this.list = d
        notifyDataSetChanged()
    }


}*/
