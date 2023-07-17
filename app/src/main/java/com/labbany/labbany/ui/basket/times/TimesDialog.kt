package com.labbany.labbany.ui.basket.times

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.DialogTimesBinding
import com.labbany.labbany.pojo.response.TimeSlotResponse
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.RecyclerViewOnClickListener
import org.koin.android.ext.android.inject

class TimesDialog(
    private val date: String,
    private val mDialogsListener: DialogsListener?
) : DialogFragment(), RecyclerViewOnClickListener {

    private lateinit var binding: DialogTimesBinding
    private lateinit var adapter: TimesAdapter
    private val viewModel: TimesViewModel by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_times,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        adapter = TimesAdapter(this)
        mContext=requireContext()

        binding.rvTimes.adapter = adapter

        times()

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mDialogsListener?.onCancel()
    }

    override fun <UI, D> onRootClickListener(dataBinding: UI, data: D) {
        super.onRootClickListener(dataBinding, data)

        data as TimeSlotResponse.TimeSlot

        mDialogsListener?.onResult(data)
        dismiss()

    }

    private fun setUp() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        isCancelable = false

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

    private fun visHeader(s: Boolean) {

        if (s) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
        }

    }

    private fun times() {

          viewModel.times(
            date
        )

        lifecycleScope.launchWhenStarted {
            viewModel.timesStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress( true)
                    }
                    is NetworkState.Error -> {
                        visProgress( false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as TimeSlotResponse

                        visProgress( false)

                        when {
                            response.success -> {

                                if (response.data.time_slot.isNullOrEmpty()) {
                                    visHeader(true)
                                } else {
                                    visHeader(false)
                                    adapter.submitData(response.data.time_slot)
                                }

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvOk.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvOk.visibility = View.VISIBLE
        }

    }


    companion object {
//        private val TAG = this::class.java.name
    }

}