package com.labbany.labbany.ui.order_details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.labbany.labbany.databinding.FragmentOrderDetailsBinding
import com.labbany.labbany.pojo.response.OrderDetailsResponse
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class OrderDetailsFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentOrderDetailsBinding
    private val viewModel: OrderDetailsViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var navController: NavController
    private var originPrice: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireView())
        mContext = requireContext()
        itemsAdapter = ItemsAdapter()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.rvMyItems.adapter = itemsAdapter

        data()

    }

    private fun data() {

        viewModel.orderDetails(
            shardHelper.id, requireArguments().getInt(Constants.ORDER_ID)
        )

        lifecycleScope.launchWhenStarted {
            viewModel.orderDetailsStateFlow.collect {

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

                                Log.e(TAG, "result: $response")
                                fillUI(response.data!!)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillUI(data: OrderDetailsResponse.Data) {

        val orderDetails = data.order[0]
        val taxPercentage = orderDetails.tax / 100
        Log.e(TAG, "fillUI: tax org ${orderDetails.tax}")
        Log.e(TAG, "fillUI: tax pre $taxPercentage")
        Log.e(TAG, "fillUI: tax ${originPrice * taxPercentage}")

        binding.tvOrderNumber.text = "${orderDetails.o_id}"
        binding.tvOrderState.text = orderDetails.order_status_str

        if (!orderDetails.cart_item.isNullOrEmpty()) {
            orderDetails.cart_item.forEach {
                originPrice += it.total_price
                itemsAdapter.submitData(it)

            }
        }

        binding.tvAddress.text = orderDetails.address.location

        binding.tvTotalPrice.text = "${orderDetails.total} ${getString(R.string.sar_2)}"

        binding.incOrderPrices.tvPriceDelivery.text =
            "${orderDetails.shipping} ${getString(R.string.sar_2)}"

        binding.incOrderPrices.tvPriceTax.text =
            "${(originPrice * taxPercentage)} ${getString(R.string.sar_2)}"

        binding.incOrderPrices.tvPriceDiscount.text =
            "${orderDetails.discount} ${getString(R.string.sar_2)}"

        binding.incOrderPrices.tvWallet.text =
            "${orderDetails.wallet_amount} ${getString(R.string.sar_2)}"

        binding.incOrderPrices.tvPriceProductOriginal.text =
            "$originPrice ${
                getString(
                    R.string.sar_2
                )
            }"

        binding.tvPaymentType.text = when (orderDetails.payment_type) {
            Constants.PaymentTypes.CASH ->
                getString(R.string.payment_when_receiving)
            /*Constants.PaymentTypes.VISA ->
                getString(R.string.payment_with_visa)*/
            else -> {
//                getString(R.string.payment_when_receiving)
                getString(R.string.payment_with_visa)
            }
        }

        if (orderDetails.order_status == Constants.Orders.DELIVERED && orderDetails.is_feedback_given == "no") {
            showRatingDialog(orderDetails.o_id)
        }

    }

    private fun showRatingDialog(oId: Int) {

        val bundle = Bundle()
        bundle.putInt(Constants.ORDER_ID, oId)

        navController.navigate(R.id.rateOrderFragment, bundle)

    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.svRoot.visibility = View.GONE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.svRoot.visibility = View.VISIBLE
        }

    }


    companion object {
        private val TAG = this::class.java.name
    }

}