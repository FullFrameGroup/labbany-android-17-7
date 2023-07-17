package com.labbany.labbany.ui.add_visa

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.PaymentServices
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VisasViewModel(private val paymentServices: PaymentServices) : ViewModel() {

    private val _addVisaStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val addVisaStateFlow: MutableStateFlow<NetworkState> get() = _addVisaStateFlow

    private val _deleteVisaStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val deleteVisaStateFlow: MutableStateFlow<NetworkState> get() = _deleteVisaStateFlow

    private val _allVisasStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val allVisasStateFlow: MutableStateFlow<NetworkState> get() = _allVisasStateFlow

    fun addVisa(
        user_id: Int,
        visa_type_id: Int,
        card_holder_name: String,
        card_number: String,
        card_date: String,
        secret_code: String
    ) {
        _addVisaStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                paymentServices.addVisa(
                    user_id,
                    visa_type_id,
                    Utils.requestBody(card_holder_name),
                    Utils.requestBody(card_number),
                    Utils.requestBody("${card_date.split("-")[1]}-${card_date.split("-")[0]}-01"),
                    Utils.requestBody(secret_code)
                )
            }.onFailure {
                _addVisaStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _addVisaStateFlow.value = NetworkState.Result(it.body())
                else
                    _addVisaStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun deleteVisa(
        user_id: Int,
        visa_id: Int
    ) {
        _deleteVisaStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                paymentServices.deleteVisa(
                    user_id,
                    visa_id
                )
            }.onFailure {
                _deleteVisaStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _deleteVisaStateFlow.value = NetworkState.Result(it.body())
                else
                    _deleteVisaStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun allVisas(
        user_id: Int,
        visa_type_id: Int?
    ) {
        _allVisasStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                paymentServices.allVisas(
                    user_id,
                    visa_type_id
                )
            }.onFailure {
                _allVisasStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _allVisasStateFlow.value = NetworkState.Result(it.body())
                else
                    _allVisasStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }
}