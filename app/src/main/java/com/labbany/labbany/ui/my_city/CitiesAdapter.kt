package com.labbany.labbany.ui.my_city

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.labbany.labbany.R
import com.labbany.labbany.databinding.RowMain2Binding
import com.labbany.labbany.pojo.model.CityModel

class CitiesAdapter : BaseAdapter() {

    private var list = ArrayList<CityModel>()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): CityModel = list[position]

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

        fun bind(data: CityModel) {

            binding.tvName.text=data.city_name_arabic
            binding.tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black))

            binding.executePendingBindings()
        }

    }

    fun submitData(d: CityModel) {
        this.list.add(d)
        notifyDataSetChanged()
    }

    fun clearData(){
        this.list.clear()
        notifyDataSetChanged()
    }

}
