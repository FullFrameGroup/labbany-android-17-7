package com.labbany.labbany.ui.auth.phone

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.pojo.model.SimpleModel
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhoneRegistrationViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _phoneRegistrationStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val phoneRegistrationStateFlow: MutableStateFlow<NetworkState> get() = _phoneRegistrationStateFlow

    fun counties(): ArrayList<SimpleModel> = arrayListOf(SimpleModel(0, ""))

    fun phoneRegistration(userId: Int, phone: String) {

        _phoneRegistrationStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.phoneRegistration(
                    userId,
                    Utils.requestBody(phone)
                )
            }.onFailure {
                _phoneRegistrationStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _phoneRegistrationStateFlow.value = NetworkState.Result(it.body())
                else
                    _phoneRegistrationStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}