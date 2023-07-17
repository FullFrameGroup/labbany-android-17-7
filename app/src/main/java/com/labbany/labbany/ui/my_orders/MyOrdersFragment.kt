package com.labbany.labbany.ui.my_orders

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentMyOrdersBinding
import com.labbany.labbany.pojo.model.Order
import com.labbany.labbany.pojo.response.OrderDetailsResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.my_orders.cancel_order.CancelOrderDialog
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class MyOrdersFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentMyOrdersBinding
    private val viewModel: MyOrdersViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_orders, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireView())
        ordersAdapter = OrdersAdapter(this)
        mContext = requireContext()

        binding.rvMyOrders.adapter = ordersAdapter

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        data()

    }

    private fun data() {

        viewModel.myOrders(shardHelper.id)

        lifecycleScope.launchWhenStarted {
            viewModel.myOrdersStateFlow.collect {

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
                        val response = it.response as OrderDetailsResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                if (response.data == null || response.data.order.isNullOrEmpty()) {
                                    binding.tvEmptyOrders.visibility = View.VISIBLE
                                } else {
                                    binding.tvEmptyOrders.visibility = View.GONE
                                    ordersAdapter.submitData(response.data.order)

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
            binding.incProgress.spinProgress.visibility = View.VISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
        }

    }

    override fun onCancelOrderClickListener(order: Order, position: Int) {

        val dialogsListener = object : DialogsListener {
            override fun onResult(success: Boolean) {

                if (success) {
                    ordersAdapter.deleteItem(order, position)
                    showHelperDialog(getString(R.string.cancel_success))

                } else {
                    showHelperDialog(getString(R.string.cancel_failure))

                }

            }
        }

        CancelOrderDialog(order.o_id, dialogsListener).show(parentFragmentManager, "")

    }

    override fun onOrderClickListener(orderId: Int) {

        val bundle = Bundle()
        bundle.putInt(Constants.ORDER_ID, orderId)

        navController.navigate(R.id.orderDetailsFragment, bundle)

    }

    private fun showHelperDialog(msg: String, mDialogsListener: DialogsListener? = null) =
        HelperDialog(msg, mDialogsListener).show(parentFragmentManager, "")

    companion object {
//        private val TAG = this::class.java.name
    }

}