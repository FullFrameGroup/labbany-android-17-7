package com.labbany.labbany.ui.auth.reset_password

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RestPasswordViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _resetPasswordStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val resetPasswordStateFlow: MutableStateFlow<NetworkState> get() = _resetPasswordStateFlow

    fun resetPassword(
        phone: String, new_password: String
    ) {
        _resetPasswordStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.resetPassword(
                    phone = Utils.requestBody(phone),
                    new_password = Utils.requestBody(new_password)
                )
            }.onFailure {
                _resetPasswordStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _resetPasswordStateFlow.value = NetworkState.Result(it.body())
                else
                    _resetPasswordStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}