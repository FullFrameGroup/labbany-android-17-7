package com.labbany.labbany.ui.wallet

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.PaymentServices
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(private val paymentServices: PaymentServices) : ViewModel() {

    private val _walletStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val walletStateFlow: MutableStateFlow<NetworkState> get() = _walletStateFlow

    fun walletDetails(userId: Int) {

        _walletStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                paymentServices.walletDetails(userId)
            }.onFailure {
                _walletStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _walletStateFlow.value = NetworkState.Result(it.body())
                else
                    _walletStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}