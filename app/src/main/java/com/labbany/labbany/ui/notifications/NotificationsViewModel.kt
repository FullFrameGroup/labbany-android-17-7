package com.labbany.labbany.ui.notifications

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.GeneralServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(private val generalServices: GeneralServices) : ViewModel() {

    private val _notificationsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val notificationsStateFlow: MutableStateFlow<NetworkState> get() = _notificationsStateFlow

    fun notifications(
         userId: Int) {

        _notificationsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                generalServices.notifications(userId)
            }.onFailure {
                _notificationsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _notificationsStateFlow.value = NetworkState.Result(it.body())
                else
                    _notificationsStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}