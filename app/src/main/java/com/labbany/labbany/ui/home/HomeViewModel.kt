package com.labbany.labbany.ui.home

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _productsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val productsStateFlow: MutableStateFlow<NetworkState> get() = _productsStateFlow

    fun products(
        cityId: Int?
    ) {
        _productsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.products(city_id = cityId)
            }.onFailure {
                _productsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _productsStateFlow.value = NetworkState.Result(it.body())
                else
                    _productsStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}