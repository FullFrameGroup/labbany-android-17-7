/*
package com.ladybug.ladybug.ui.add_farm.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.ladybug.ladybug.R
import com.ladybug.ladybug.databinding.ItemSpinnerColorBinding
import com.aait.nokhba2.pojo.model.SimpleModel

class ItemsAdapter : BaseAdapter() {

    private var list = ArrayList<SimpleModel>()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mBinding = DataBindingUtil.inflate<ItemSpinnerColorBinding>(
            LayoutInflater.from(parent!!.context!!),
            R.layout.item_spinner_color, parent, false
        )

        val mViewHolder = ViewHolder(mBinding)

        mViewHolder.bind(list[position])

        return mBinding.root
    }


    inner class ViewHolder(private val binding: ItemSpinnerColorBinding) {

        fun bind(data: SimpleModel) {

            binding.tv.text=data.name

            binding.executePendingBindings()
        }

    }

    fun submitData(d: SimpleModel){
        this.list.add(d)
        notifyDataSetChanged()
    }

    fun clearData(){
        this.list.clear()
        notifyDataSetChanged()
    }

}*/
