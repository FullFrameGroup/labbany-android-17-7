package com.labbany.labbany.ui.auth.change_password

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _changePasswordStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val changePasswordStateFlow: MutableStateFlow<NetworkState> get() = _changePasswordStateFlow

    fun changePassword(
        id: Int, old_password: String, new_password: String
    ) {
        _changePasswordStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.changePassword(
                    id,
                    old_password = Utils.requestBody(old_password),
                    new_password = Utils.requestBody(new_password)
                )
            }.onFailure {
                _changePasswordStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _changePasswordStateFlow.value = NetworkState.Result(it.body())
                else
                    _changePasswordStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }


}