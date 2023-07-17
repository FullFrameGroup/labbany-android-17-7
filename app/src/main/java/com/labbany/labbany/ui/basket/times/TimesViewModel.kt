package com.labbany.labbany.ui.basket.times

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TimesViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _timesStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val timesStateFlow: MutableStateFlow<NetworkState> get() = _timesStateFlow

    fun times(date: String) {
        _timesStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.timeSlot(date)
            }.onFailure {
                _timesStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _timesStateFlow.value = NetworkState.Result(it.body())
                else
                    _timesStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}