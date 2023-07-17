package com.labbany.labbany.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemHomeHeaderBinding
import com.labbany.labbany.pojo.model.Category
import com.labbany.labbany.util.RecyclerViewOnClickListener

class HomeHeaderAdapter(private val listener: RecyclerViewOnClickListener) :
    RecyclerView.Adapter<HomeHeaderAdapter.ViewHolder>() {


    private var list = ArrayList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_home_header,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemHomeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {

//            val mContext = binding.root.context
            val adapter = HomeBodyAdapter(listener)

            binding.tvName.text = category.categoryName

         /*   binding.rvProducts.layoutManager =
                object : LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false) {
                    override fun isLayoutRTL(): Boolean {
                        return true
                    }
                }
*/
            binding.rvProducts.adapter = adapter

            adapter.clear()
            adapter.submitData(category.products!!)


            binding.executePendingBindings()
        }

    }

    fun clear(){
        this.list.clear()
        notifyDataSetChanged()
    }

    fun submitData(d: Category) {
        this.list.add( d)
        notifyDataSetChanged()
    }

}