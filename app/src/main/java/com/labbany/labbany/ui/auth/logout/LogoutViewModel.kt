package com.labbany.labbany.ui.auth.logout

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LogoutViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _logoutStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val logoutStateFlow: MutableStateFlow<NetworkState> get() = _logoutStateFlow

    fun logout(id: Int) {

        _logoutStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.logout(
                    id
                )
            }.onFailure {
                _logoutStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _logoutStateFlow.value = NetworkState.Result(it.body())
                else
                    _logoutStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}