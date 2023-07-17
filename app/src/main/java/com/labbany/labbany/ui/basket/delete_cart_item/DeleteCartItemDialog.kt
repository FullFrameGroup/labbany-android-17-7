package com.labbany.labbany.ui.basket.delete_cart_item

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.ShardHelper
import org.koin.android.ext.android.inject

class DeleteCartItemDialog(
    private val cartId: Int, private val cartItemId: Int,
    private val mDialogsListener: DialogsListener?
) : DialogFragment() {

    private lateinit var binding: DialogDeleteItemBinding
    private val viewModel: DeleteCartItemViewModel by inject()
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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false

        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        /*  val window: Window = dialog!!.window!!
         val wlp: WindowManager.LayoutParams = window.attributes

         wlp.gravity = Gravity.CENTER
         wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
         window.attributes = wlp
 */
    }

    private fun delete() {

        viewModel.deleteCartItem(
            shardHelper.id, cartId, cartItemId
        )

        lifecycleScope.launchWhenStarted {
            viewModel.deleteCartItemStateFlow.collect {

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
                                mDialogsListener?.onResult()
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

    companion object {
//        private val TAG = this::class.java.name
    }

}