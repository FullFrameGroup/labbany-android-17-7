package com.labbany.labbany.ui.auth.otp

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

class OtpViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _sendOrResendOtpStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val sendOrResendOtpStateFlow: MutableStateFlow<NetworkState> get() = _sendOrResendOtpStateFlow

    private val _codeCheckStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val codeCheckStateFlow: MutableStateFlow<NetworkState> get() = _codeCheckStateFlow

    private val _forgePasswordStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val forgePasswordStateFlow: MutableStateFlow<NetworkState> get() = _forgePasswordStateFlow

    fun sendOrResendOtp(
        userId: Int, phone: String
    ) {

        _sendOrResendOtpStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.sendOrResendOtp(
                    userId,
                    Utils.requestBody(phone)
                )
            }.onFailure {
                _sendOrResendOtpStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _sendOrResendOtpStateFlow.value = NetworkState.Result(it.body())
                else
                    _sendOrResendOtpStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun codeCheck(
        userId: Int, otp: String, is_registration: Int
    ) {

        _codeCheckStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.codeCheck(
                    id = userId,
                    otp = Utils.requestBody(otp),
                    is_registration = is_registration
                )
            }.onFailure {
                _codeCheckStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _codeCheckStateFlow.value = NetworkState.Result(it.body())
                else
                    _codeCheckStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun forgePassword(
        phone: String
    ) {

        _forgePasswordStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.forgePassword(
                    phone = Utils.requestBody(phone)
                )
            }.onFailure {
                _forgePasswordStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _forgePasswordStateFlow.value = NetworkState.Result(it.body())
                else
                    _forgePasswordStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun counties(): ArrayList<SimpleModel> = arrayListOf(SimpleModel(0, ""))


    companion object {
//        private val TAG = this::class.java.name
    }


}