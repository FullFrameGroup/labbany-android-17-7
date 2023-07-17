package com.labbany.labbany.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AuthServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val authServices: AuthServices) : ViewModel() {

    private val _profileStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val profileStateFlow: MutableStateFlow<NetworkState> get() = _profileStateFlow

    private val _editProfileStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val editProfileStateFlow: MutableStateFlow<NetworkState> get() = _editProfileStateFlow

    fun profile(
        id: Int
    ) {
        _profileStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.profile(
                    id
                )
            }.onFailure {
                _profileStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _profileStateFlow.value = NetworkState.Result(it.body())
                else
                    _profileStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun editProfile(
        mContext: Context, id: Int, name: String, email: String, image: Uri?
    ) {
        _editProfileStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                authServices.updateProfile(
                    id,
                    Utils.requestBody(name),
                    Utils.requestBody(email),
                    if (image != null) Utils.imageBody(mContext, image, "image") else null,
                )
            }.onFailure {
                _editProfileStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _editProfileStateFlow.value = NetworkState.Result(it.body())
                else
                    _editProfileStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}