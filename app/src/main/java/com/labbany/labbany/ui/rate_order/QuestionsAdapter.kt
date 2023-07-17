package com.labbany.labbany.ui.rate_order

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.labbany.labbany.R
import com.labbany.labbany.databinding.ItemQuestionBinding
import com.labbany.labbany.pojo.request.RateOrderRequest
import com.labbany.labbany.pojo.response.QuestionsResponse
import com.labbany.labbany.util.Constants

class QuestionsAdapter :
    RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {


    private var list = ArrayList<QuestionsResponse.FeedbackQuestion>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_question,
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: QuestionsResponse.FeedbackQuestion) {

            val mContext = binding.root.context

            binding.tvQuestion.text = data.title
            data.answer = true

            binding.tvYes.setOnClickListener {

                if (data.answer) return@setOnClickListener

                data.answer = true

                select(mContext, binding.tvYes)
                deSelect(mContext, binding.tvNo)

            }

            binding.tvNo.setOnClickListener {

                if (!data.answer) return@setOnClickListener

                data.answer = false

                select(mContext, binding.tvNo)
                deSelect(mContext, binding.tvYes)

            }

            binding.executePendingBindings()
        }

        private fun select(mContext: Context, tv: TextView) {

            tv.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.bg_solid_gray_corner_7sdp_border_red_1dp
            )

            tv.setTextColor(ContextCompat.getColor(mContext, R.color.dull_red))

        }

        private fun deSelect(mContext: Context, tv: TextView) {

            tv.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.bg_solid_gray_corner_7sdp
            )

            tv.setTextColor(ContextCompat.getColor(mContext, R.color.black))

        }

    }

    fun answers(): List<RateOrderRequest.Answer> {

        val ans = ArrayList<RateOrderRequest.Answer>()

        this.list.forEach {
            ans.add(
                RateOrderRequest.Answer(
                    if (it.answer) Constants.Answers.GOOD else Constants.Answers.BAD,
                    it.id
                )
            )
        }
        return ans
    }

    fun isEmpty(): Boolean = this.list.isNullOrEmpty()

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(d: ArrayList<QuestionsResponse.FeedbackQuestion>) {
        this.list = d
        notifyDataSetChanged()
    }

}