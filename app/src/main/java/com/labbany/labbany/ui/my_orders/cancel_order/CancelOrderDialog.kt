package com.labbany.labbany.ui.my_orders.cancel_order

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.DialogDeleteItemBinding
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.my_orders.MyOrdersViewModel
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.ShardHelper
import org.koin.android.ext.android.inject


class CancelOrderDialog(
    private val orderId: Int,
    private val mDialogsListener: DialogsListener?
) : DialogFragment() {

    private lateinit var binding: DialogDeleteItemBinding
    private val viewModel: MyOrdersViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_delete_item,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        mContext = requireContext()

        binding.tvOk.setOnClickListener {

            delete()

        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

    }

    private fun setUp() {

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false

//        val window: Window = dialog!!.window!!
//        val wlp: WindowManager.LayoutParams = window.attributes

//        wlp.gravity = Gravity.CENTER
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
//        window.attributes = wlp

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    }

    private fun delete() {

        viewModel.cancelOrder(
            shardHelper.id, orderId
        )

        lifecycleScope.launchWhenStarted {
            viewModel.cancelOrderStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as GeneralResponse

                        visProgress(false)

                        when {
                            response.success -> {
                                mDialogsListener?.onResult(true)
                                dismiss()
                            }
                            response.code == Constants.Orders.CANCEL_ORDER -> {

                                mDialogsListener?.onResult(false)
                                dismiss()
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


}