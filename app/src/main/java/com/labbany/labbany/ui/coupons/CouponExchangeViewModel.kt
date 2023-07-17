package com.labbany.labbany.ui.coupons

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.PaymentServices
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CouponExchangeViewModel(private val paymentServices: PaymentServices) : ViewModel() {

    private val _checkWalletCouponStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val checkWalletCouponStateFlow: MutableStateFlow<NetworkState> get() = _checkWalletCouponStateFlow

    fun checkWalletCoupon(
        user_id: Int,
        coupon_code: String,
        city_id: Int
    ) {
        _checkWalletCouponStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                paymentServices.checkWalletCoupon(user_id, Utils.requestBody(coupon_code), city_id)
            }.onFailure {
                _checkWalletCouponStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _checkWalletCouponStateFlow.value = NetworkState.Result(it.body())
                else
                    _checkWalletCouponStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)


            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}