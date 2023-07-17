package com.labbany.labbany.ui.views

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow

class SharedViewModel : ViewModel() {

    private var _visaStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    var visaStateFlow = _visaStateFlow

    private val _addressUpdateState = MutableStateFlow(false)
    val addressUpdateState = _addressUpdateState

    private val _selectAddressForOrderState = MutableStateFlow(false)
    val selectAddressForOrderState = _selectAddressForOrderState

    private val _selectAddressData = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val selectAddressData: MutableStateFlow<NetworkState> get() = _selectAddressData

    private val _visaAddedStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val visaAddedStateFlow = _visaAddedStateFlow

}