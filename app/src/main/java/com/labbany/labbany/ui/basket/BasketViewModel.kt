package com.labbany.labbany.ui.basket

import android.util.Log
import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.HyperPayServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.pojo.request.MakeOrderRequest
import com.labbany.labbany.pojo.response.AddressesResponse
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BasketViewModel(
    private val ordersServices: OrdersServices,
    private val hyperPayServices: HyperPayServices
) : ViewModel() {

    private val _cartDetailStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val cartDetailStateFlow: MutableStateFlow<NetworkState> get() = _cartDetailStateFlow

    private val _makeOrderStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val makeOrderStateFlow: MutableStateFlow<NetworkState> get() = _makeOrderStateFlow

    private val _checkOrderCouponStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val checkOrderCouponStateFlow: MutableStateFlow<NetworkState> get() = _checkOrderCouponStateFlow

    private val _sendPaymentDetailStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val sendPaymentDetailStateFlow: MutableStateFlow<NetworkState> get() = _sendPaymentDetailStateFlow

    private val _getawayResultStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val getawayResultStateFlow: MutableStateFlow<NetworkState> get() = _getawayResultStateFlow

    private val _checkoutIdStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val checkoutIdStateFlow: MutableStateFlow<NetworkState> get() = _checkoutIdStateFlow

    fun cartDetail(
        userId: Int
    ) {

        _cartDetailStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.cartDetail(userId)
            }.onFailure {
                _cartDetailStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

              //  Log.e(TAG, "cartDetail: ${it.body()}")

                if (it.body() != null)
                    _cartDetailStateFlow.value = NetworkState.Result(it.body())
                else
                    _cartDetailStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun makeOrder(
        makeOrderRequest: MakeOrderRequest
    ) {
        _makeOrderStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.makeOrder(makeOrderRequest)
            }.onFailure {
              //  Log.e(TAG, "makeOrder1: $it")
                _makeOrderStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

              //  Log.e(TAG, "makeOrder2: $it")
//              //  Log.e(TAG, "makeOrder2: $makeOrderRequest")
//              //  Log.e(TAG, "makeOrder2: ${it.body()}")

                if (it.body() != null)
                    _makeOrderStateFlow.value = NetworkState.Result(it.body())
                else
                    _makeOrderStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun checkOrderCoupon(
        user_id: Int,
        coupon_code: String,
        city_id: Int
    ) {

        _checkOrderCouponStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.checkOrderCoupon(user_id, Utils.requestBody(coupon_code), city_id)
            }.onFailure {
                _checkOrderCouponStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _checkOrderCouponStateFlow.value = NetworkState.Result(it.body())
                else
                    _checkOrderCouponStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun requestCheckoutId(
        user_id: Int,
        user_name: String,
        email: String,
        city: String,
        amount: Double,
        cartId: Int,
        payment_method: String,
        addressModel: AddressesResponse.Address
    ) {

        _checkoutIdStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                hyperPayServices.requestCheckoutId(
                    customerId = user_id,
                    customer_email = Utils.requestBody(email),
                    billing_city = Utils.requestBody(city), amount = amount,
                    merchantTransactionId = Utils.requestBody("cart$cartId"),
                    merchantInvoiceId = Utils.requestBody("cart$cartId"),
                    payment_method = Utils.requestBody(payment_method),
                    given_name = Utils.requestBody(user_name),
                    surname = Utils.requestBody(user_name),
                    billing_street1 = Utils.requestBody(addressModel.district_name),
                )
            }.onFailure {
                _checkoutIdStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
//              //  Log.e(TAG, "requestCheckoutId: ${it.message}")
            }.onSuccess {

//              //  Log.e(TAG, "requestCheckoutId: ${it.body()}")

                if (it.body() != null)
                    _checkoutIdStateFlow.value = NetworkState.Result(it.body())
                else
                    _checkoutIdStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun paymentStatus(
        resourcePath: String, payment_method: String
    ) {

        _getawayResultStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                hyperPayServices.requestPaymentStatus(
                    resourcePath = Utils.requestBody(resourcePath),
                    Utils.requestBody(payment_method)
                )
            }.onFailure {
                _getawayResultStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

//                Log.e(BasketActivity.TAG, "hyper pay transaction: vm 2 ${it.body()}")

                if (it.body() != null)
                    _getawayResultStateFlow.value = NetworkState.Result(it.body())
                else
                    _getawayResultStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun sendPaymentDetails(
        user_id: Int,
        total: Double,
        transaction_id: String?,
        checkoutId: String,
        payment_details: String,
        card_number: String,
        card_holder_name: String,
        card_expair_date: String,
        order_id: Int,

        ) {

        _sendPaymentDetailStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.paymentDetails(
                    user_id = user_id,
                    card_amt = total,
                    transaction_id = if (transaction_id.isNullOrEmpty()) null
                    else
                        Utils.requestBody(transaction_id),
                    checkoutId = Utils.requestBody(checkoutId),
                    payment_info = Utils.requestBody(payment_details),
                    card_number = Utils.requestBody(card_number),
                    card_holdername = Utils.requestBody(card_holder_name),
                    card_expairdate = Utils.requestBody(card_expair_date),
                    order_id = order_id
                )
            }.onFailure {
                _sendPaymentDetailStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

              //  Log.e(TAG, "sendPaymentDetails: ${it.body()}")

                if (it.body() != null)
                    _sendPaymentDetailStateFlow.value = NetworkState.Result(it.body())
                else
                    _sendPaymentDetailStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }

}