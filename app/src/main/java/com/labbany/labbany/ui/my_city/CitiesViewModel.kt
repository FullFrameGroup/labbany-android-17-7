package com.labbany.labbany.ui.my_city

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.AddressesServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CitiesViewModel(private val addressesServices: AddressesServices) : ViewModel() {

    private val _citiesStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val citiesStateFlow: MutableStateFlow<NetworkState> get() = _citiesStateFlow

    private val _setCityStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val setCityStateFlow: MutableStateFlow<NetworkState> get() = _setCityStateFlow

    fun cities(
    ) {

        _citiesStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.cities()
            }.onFailure {
                _citiesStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _citiesStateFlow.value = NetworkState.Result(it.body())
                else
                    _citiesStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun setCity(
        id: Int, city_id: Int
    ) {

        _setCityStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.setCity(
                    id, city_id
                )
            }.onFailure {
                _setCityStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _setCityStateFlow.value = NetworkState.Result(it.body())
                else
                    _setCityStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }


}