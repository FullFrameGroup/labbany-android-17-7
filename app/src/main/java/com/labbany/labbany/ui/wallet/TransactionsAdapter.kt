package com.labbany.labbany.ui.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemTransactionDownBinding
import com.labbany.labbany.databinding.ItemTransactionUpBinding
import com.labbany.labbany.pojo.response.WalletResponse
import com.labbany.labbany.util.Constants

class TransactionsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var list = ArrayList<WalletResponse.Wallet>()

    override fun getItemViewType(position: Int): Int =
        if (list[position].amount > 0) Constants.TRANSACTIONS_UP else Constants.TRANSACTIONS_DOWN

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == Constants.TRANSACTIONS_UP)
            UpViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_transaction_up,
                    parent, false
                )
            )
        else
            DownViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_transaction_down,
                    parent, false
                )
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is UpViewHolder)
            holder.bindUp(list[position])
        else if (holder is DownViewHolder)
            holder.bindDown(list[position])

    }

    override fun getItemCount(): Int = list.size

    inner class UpViewHolder(val binding: ItemTransactionUpBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindUp(data: WalletResponse.Wallet) {

            binding.tvDate.text = data.date_time
            binding.tvMessage.text = data.description

            binding.executePendingBindings()
        }

    }

    inner class DownViewHolder(val binding: ItemTransactionDownBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindDown(data: WalletResponse.Wallet) {

            binding.tvDate.text = data.date_time
            binding.tvMessage.text = data.description

            binding.executePendingBindings()
        }

    }

    fun submitData(d: ArrayList<WalletResponse.Wallet>) {
        this.list = d
        notifyDataSetChanged()
    }

}