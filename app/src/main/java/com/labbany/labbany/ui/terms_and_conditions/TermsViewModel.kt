package com.labbany.labbany.ui.terms_and_conditions

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.GeneralServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TermsViewModel(private val generalServices: GeneralServices) : ViewModel() {

    private val _termsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val termsStateFlow: MutableStateFlow<NetworkState> get() = _termsStateFlow

    fun terms() {

        _termsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                generalServices.terms()
            }.onFailure {

                _termsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)

            }.onSuccess {

                if (it.body() != null)
                    _termsStateFlow.value = NetworkState.Result(it.body())
                else
                    _termsStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}