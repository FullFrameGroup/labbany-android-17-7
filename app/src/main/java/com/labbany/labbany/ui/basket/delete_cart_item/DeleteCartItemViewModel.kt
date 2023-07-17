package com.labbany.labbany.ui.basket.delete_cart_item

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DeleteCartItemViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _deleteCartItemStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val deleteCartItemStateFlow: MutableStateFlow<NetworkState> get() = _deleteCartItemStateFlow

    fun deleteCartItem(
        user_id: Int,
        cartId: Int, cartItemId: Int,
    ) {
        _deleteCartItemStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.deleteCartItem(user_id, cartId, cartItemId)
            }.onFailure {
                _deleteCartItemStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _deleteCartItemStateFlow.value = NetworkState.Result(it.body())
                else
                    _deleteCartItemStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}