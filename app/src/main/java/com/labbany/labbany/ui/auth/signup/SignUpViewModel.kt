package com.labbany.labbany.ui.auth.signup

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _signUpStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val signUpStateFlow: MutableStateFlow<NetworkState> get() = _signUpStateFlow

    fun signUP(
        mContext: Context,
        name: String,
        email: String,
        password: String,
        image: Uri?,
        bitmap: Bitmap?
    ) {
        _signUpStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    // Get new FCM registration token
                    val token = if (!task.isSuccessful)
                        "token"
                    else
                        task.result

                    CoroutineScope(Dispatchers.IO).launch {
                        completeSignUP(
                            mContext, name, email, password, image, bitmap, token
                        )
                    }

                }

            }
        }
    }

    private fun completeSignUP(
        mContext: Context,
        name: String,
        email: String,
        password: String,
        image: Uri?,
        bitmap: Bitmap?,
        fcm_token: String?
    ) {
        //  Log.e(TAG, "completeSignUP: image ${image != null}")

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.register(
                    Utils.requestBody(name),
                    Utils.requestBody(email),
                    Utils.requestBody(password),
                    Utils.requestBody(password),
                    Utils.requestBody("$fcm_token"),
                    when {
                        image != null -> Utils.imageBody(
                            mContext,
                            image,
                            "image"
                        )

                        bitmap != null -> Utils.imageBody(bitmap, "image")
                        else -> null
                    },
                    Utils.requestBody(Constants.DEVICE_TYPE),
                    Utils.requestBody(Constants.APP_VERSION)
                )
            }.onFailure {
                Log.e(TAG, "completeSignUP1: ${it.message}")
                _signUpStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {
                Log.e(TAG, "completeSignUP2: ${it.message()}")
                Log.e(TAG, "completeSignUP3: ${it.code()}")
                if (it.body() != null)
                    _signUpStateFlow.value = NetworkState.Result(it.body())
                else
                    _signUpStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }
}