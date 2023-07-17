package com.labbany.labbany.ui.add_address

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.labbany.labbany.R
import com.labbany.labbany.databinding.RowMainBinding
import com.labbany.labbany.pojo.model.CityModel

class CitiesAdapter2 : BaseAdapter() {

    private var list = ArrayList<CityModel>()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): CityModel = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mBinding = DataBindingUtil.inflate<RowMainBinding>(
            LayoutInflater.from(parent!!.context!!),
            R.layout.row_main, parent, false
        )

        val mViewHolder = ViewHolder(mBinding)

        mViewHolder.bind(list[position])

        return mBinding.root
    }


    inner class ViewHolder(private val binding: RowMainBinding) {

        fun bind(data: CityModel) {

            binding.tvName.text = data.city_name_arabic

            binding.executePendingBindings()
        }

    }

    fun submitData(d: CityModel) {
        this.list.add(d)
        notifyDataSetChanged()
    }

}
