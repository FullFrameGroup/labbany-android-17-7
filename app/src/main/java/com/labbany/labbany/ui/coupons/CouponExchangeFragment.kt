package com.labbany.labbany.ui.coupons

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
import com.labbany.labbany.databinding.FragmentCouponExchangeBinding
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class CouponExchangeFragment : Fragment() {

    private lateinit var binding: FragmentCouponExchangeBinding
    private lateinit var navController: NavController
    private lateinit var mContext: Context
    private val viewModel: CouponExchangeViewModel by inject()
    private val shardHelper: ShardHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_coupon_exchange, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext = requireContext()

        binding.tvSave.setOnClickListener {

          //  Log.e(TAG, "onViewCreated: 1")

            if (!verify()) return@setOnClickListener

          //  Log.e(TAG, "onViewCreated: 2")

            callApi()

        }

    }

    private fun callApi() {

        viewModel.checkWalletCoupon(
            shardHelper.id,
            binding.etCoupon.text.toString(),
            shardHelper.cityId
        )

        lifecycleScope.launchWhenStarted {
            viewModel.checkWalletCouponStateFlow.collect {

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

                                val mDialogsListener = object : DialogsListener {
                                    override fun onDismiss() {
                                        navController.popBackStack()
                                    }
                                }
                                showHelperDialog(
                                    getString(R.string.success_coupon),
                                    mDialogsListener
                                )
                            }
                            response.code == Constants.Coupons.Not_Found_CODE -> {
                                showHelperDialog(getString(R.string.not_found_coupon), null)
                            }
                            response.code == Constants.Coupons.EXPIRED_CODE -> {
                                showHelperDialog(getString(R.string.expired_coupon), null)
                            }
                            response.code == Constants.Coupons.CITY_CODE -> {
                                showHelperDialog(getString(R.string.no_city_coupon), null)
                            }
                            response.code == Constants.Coupons.MAX_CODE -> {
                                showHelperDialog(getString(R.string.max_coupon), null)
                            }
                            response.code == Constants.Coupons.NO_WALLET_CODE -> {
                                showHelperDialog(getString(R.string.not_wallet_coupon), null)
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etCoupon, getString(R.string.enter_coupon_code))) isNull =
            true

      //  Log.e(TAG, "verify: $isNull")

        return !isNull
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvSave.visibility = View.INVISIBLE
            binding.etCoupon.isEnabled = false
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvSave.visibility = View.VISIBLE
            binding.etCoupon.isEnabled = true
        }

    }

    private fun showHelperDialog(msg: String, mDialogsListener: DialogsListener?) =
        HelperDialog(msg, mDialogsListener).show(parentFragmentManager, "")


    companion object {
        private val TAG = this::class.java.name
    }

}