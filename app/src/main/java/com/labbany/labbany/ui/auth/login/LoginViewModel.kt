package com.labbany.labbany.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _normalLoginStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val normalLoginStateFlow: MutableStateFlow<NetworkState> get() = _normalLoginStateFlow

    private val _socialLoginStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val socialLoginStateFlow: MutableStateFlow<NetworkState> get() = _socialLoginStateFlow

    fun login(
        phoneOrEmail: String,
        password: String?,
        loginType: Int
    ) {

        if (loginType == Constants.AuthTypes.NORMAL)
            _normalLoginStateFlow.value = NetworkState.Loading
        else
            _socialLoginStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
//                      //  Log.e(TAG, "Fetching FCM registration token failed", task.exception)

                        completeNormalLogin(
                            phoneOrEmail, password!!, "token"
                        )
                        /* if (loginType == Constants.AuthTypes.NORMAL)
                            _normalLoginStateFlow.value =
                                NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
                        else
                            _socialLoginStateFlow.value =
                                NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
*/
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    if (loginType == Constants.AuthTypes.NORMAL)
                        CoroutineScope(Dispatchers.IO).launch {
                            completeNormalLogin(
                                phoneOrEmail, password!!, token
                            )
                        }
                    else CoroutineScope(Dispatchers.IO).launch {
                        completeSocialLogin(
                            phoneOrEmail, token
                        )
                    }

                })

            }
        }
    }

    private fun completeNormalLogin(
        phone: String,
        password: String,
        fcm_token: String?
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.login(
                    Utils.requestBody(phone),
                    Utils.requestBody(password),
                    Utils.requestBody("$fcm_token"),
                    Utils.requestBody(Constants.DEVICE_TYPE),
                    Utils.requestBody(Constants.APP_VERSION)
                )
            }.onFailure {
                _normalLoginStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _normalLoginStateFlow.value = NetworkState.Result(it.body())
                else
                    _normalLoginStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    private fun completeSocialLogin(
        email: String,
        fcm_token: String?
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.social(
                    Utils.requestBody(email),
                    Utils.requestBody("$fcm_token"),
                    Utils.requestBody(Constants.DEVICE_TYPE),
                    Utils.requestBody(Constants.APP_VERSION)
                )
            }.onFailure {
                _socialLoginStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _socialLoginStateFlow.value = NetworkState.Result(it.body())
                else
                    _socialLoginStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }
}