package com.labbany.labbany.ui.order_details

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _orderDetailsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val orderDetailsStateFlow: MutableStateFlow<NetworkState> get() = _orderDetailsStateFlow

    fun orderDetails(
        user_id: Int, order_id: Int
    ) {
        _orderDetailsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.orderDetails(user_id, order_id)
            }.onFailure {
                _orderDetailsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
         }.onSuccess {

                if (it.body() != null)
                    _orderDetailsStateFlow.value = NetworkState.Result(it.body())
                else
                    _orderDetailsStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}