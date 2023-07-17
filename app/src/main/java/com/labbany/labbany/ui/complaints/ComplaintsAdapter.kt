package com.labbany.labbany.ui.complaints

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemComplaintSolvedBinding
import com.labbany.labbany.databinding.ItemComplaintUnSolvedBinding
import com.labbany.labbany.pojo.response.ComplaintsResponse
import com.labbany.labbany.util.Constants

class ComplaintsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var list = ArrayList<ComplaintsResponse.Complaint>()

    override fun getItemViewType(position: Int): Int =
        if (list[position].is_reviewed == 1) Constants.SOLVED else Constants.UN_SOLVED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == Constants.SOLVED)
            SolvedViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_complaint_solved,
                    parent, false
                )
            )
        else
            UnSolvedViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_complaint_un_solved,
                    parent, false
                )
            )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SolvedViewHolder)
            holder.bindUp(list[position])
        else if (holder is UnSolvedViewHolder)
            holder.bindDown(list[position])

    }

    override fun getItemCount(): Int = list.size

    inner class SolvedViewHolder(val binding: ItemComplaintSolvedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindUp(data: ComplaintsResponse.Complaint) {

            val mContext = binding.root.context

            binding.tvNumber.text =
                mContext.getString(R.string.complaint_num) + "#" + data.complaint_number

            binding.tvDate.text = data.complaint_date
            binding.tvDetails.text = data.complaint_description
            binding.tvTitle.text = data.complaint_title

            binding.executePendingBindings()
        }

    }

    inner class UnSolvedViewHolder(val binding: ItemComplaintUnSolvedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindDown(data: ComplaintsResponse.Complaint) {

            val mContext = binding.root.context

            binding.tvNumber.text =
                mContext.getString(R.string.complaint_num) + "#" + data.complaint_number

            binding.tvDate.text = data.complaint_date
            binding.tvDetails.text = data.complaint_description
            binding.tvTitle.text = data.complaint_title

            binding.executePendingBindings()
        }

    }

    fun submitData(d: List<ComplaintsResponse.Complaint>?) {
        this.list = d as ArrayList<ComplaintsResponse.Complaint>
        notifyDataSetChanged()
    }

}