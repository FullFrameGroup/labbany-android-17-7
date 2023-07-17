package com.labbany.labbany.ui.my_orders

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MyOrdersViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _myOrdersStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val myOrdersStateFlow: MutableStateFlow<NetworkState> get() = _myOrdersStateFlow

    private val _cancelOrderStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val cancelOrderStateFlow: MutableStateFlow<NetworkState> get() = _cancelOrderStateFlow

    fun myOrders(
        user_id: Int
    ) {
        _myOrdersStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.myOrders(user_id)
            }.onFailure {
                _myOrdersStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _myOrdersStateFlow.value = NetworkState.Result(it.body())
                else
                    _myOrdersStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }


    fun cancelOrder(
        user_id: Int, order_id: Int
    ) {
        _cancelOrderStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.cancelOrder(user_id, order_id)
            }.onFailure {
                _cancelOrderStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _cancelOrderStateFlow.value = NetworkState.Result(it.body())
                else
                    _cancelOrderStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}