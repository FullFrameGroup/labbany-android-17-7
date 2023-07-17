package com.labbany.labbany.ui.complaints

import android.util.Log
import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.ComplaintsServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ComplaintsViewModel(private val complaintsServices: ComplaintsServices) : ViewModel() {

    private val _complaintsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val complaintsStateFlow: MutableStateFlow<NetworkState> get() = _complaintsStateFlow

    private val _complaintTypesStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val complaintTypesStateFlow: MutableStateFlow<NetworkState> get() = _complaintTypesStateFlow

    private val _uploadComplaintStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val uploadComplaintStateFlow: MutableStateFlow<NetworkState> get() = _uploadComplaintStateFlow

    fun complaints(
        userId: Int
    ) {

        _complaintsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                complaintsServices.complaints(userId)
            }.onFailure {
                _complaintsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                Log.e(TAG, "complaints: $it")
                Log.e(TAG, "complaints: ${it.body()}")
                if (it.body() != null)
                    _complaintsStateFlow.value = NetworkState.Result(it.body())
                else
                    _complaintsStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun complaintTypes() {

        _complaintTypesStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                complaintsServices.complaintTypes()
            }.onFailure {
                _complaintTypesStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _complaintTypesStateFlow.value = NetworkState.Result(it.body())
                else
                    _complaintTypesStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun uploadComplaint(
        userId: Int,
        name: String,
        phone: String,
        complaintTypeId: Int,
        description: String
    ) {

        _uploadComplaintStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                complaintsServices.uploadComplaint(
                    userId,
                    Utils.requestBody(name),
                    Utils.requestBody(phone),
                    complaintTypeId,
                    Utils.requestBody(description),
                )
            }.onFailure {
                _uploadComplaintStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _uploadComplaintStateFlow.value = NetworkState.Result(it.body())
                else
                    _uploadComplaintStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }
}