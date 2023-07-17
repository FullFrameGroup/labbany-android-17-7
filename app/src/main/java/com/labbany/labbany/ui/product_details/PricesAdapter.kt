package com.labbany.labbany.ui.product_details

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.labbany.labbany.R
import com.labbany.labbany.databinding.RowMain2Binding
import com.labbany.labbany.pojo.model.PriceModel

class PricesAdapter : BaseAdapter() {

    private var list = ArrayList<PriceModel>()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): PriceModel {
        Log.e(TAG, "getItem: price ${list[position].price}")
        return list[position]
    }
    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mBinding = DataBindingUtil.inflate<RowMain2Binding>(
            LayoutInflater.from(parent!!.context!!),
            R.layout.row_main_2, parent, false
        )

        val mViewHolder = ViewHolder(mBinding)

        mViewHolder.bind(list[position])

        return mBinding.root
    }


    inner class ViewHolder(private val binding: RowMain2Binding) {

        fun bind(data: PriceModel) {

            binding.tvName.text=data.name
            binding.tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black))

            binding.executePendingBindings()
        }

    }

    fun submitData(d: PriceModel) {
        this.list.add(d)
        notifyDataSetChanged()
    }

    fun clearData(){
        this.list.clear()
        notifyDataSetChanged()
    }

    companion object{
        private val TAG=this::class.java.name
    }

}
