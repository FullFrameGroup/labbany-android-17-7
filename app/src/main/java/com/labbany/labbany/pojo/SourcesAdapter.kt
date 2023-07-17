package com.labbany.labbany.pojo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemHomeBodyBinding
import com.labbany.labbany.pojo.model.SimpleModel
import com.labbany.labbany.util.RecyclerViewOnClickListener

class SourcesAdapter(val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {


    private var list = ArrayList<SimpleModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_home_body,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemHomeBodyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SimpleModel) {


            binding.executePendingBindings()
        }

    }

    fun submitData(d: SimpleModel) {
        this.list.add(d)
        notifyDataSetChanged()
    }

    }