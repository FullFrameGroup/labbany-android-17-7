package com.labbany.labbany.ui.auth.forget_password

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.labbany.labbany.R
import com.labbany.labbany.databinding.RowCountryBinding
import com.labbany.labbany.pojo.model.SimpleModel

class CountriesAdapter : BaseAdapter() {

    private var list = ArrayList<SimpleModel>()

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): SimpleModel = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mBinding = DataBindingUtil.inflate<RowCountryBinding>(
            LayoutInflater.from(parent!!.context!!),
            R.layout.row_country, parent, false
        )

        val mViewHolder = ViewHolder(mBinding)

        mViewHolder.bind(/*list[position]*/)

        return mBinding.root
    }


    inner class ViewHolder(private val binding: RowCountryBinding) {

        fun bind(/*data: SimpleModel*/) {

         /*   binding.tvName.text=data.city_name_arabic
            binding.tvName.setTextColor(ContextCompat.getColor(binding.root.context,R.color.black))
*/
            binding.executePendingBindings()
        }

    }

    fun submitData(d: SimpleModel) {
        this.list.add(d)
        notifyDataSetChanged()
    }

    fun submitData(d: ArrayList<SimpleModel>) {
        this.list.addAll(d)
        notifyDataSetChanged()
    }

   /* fun clearData(){
        this.list.clear()
        notifyDataSetChanged()
    }
*/
}
