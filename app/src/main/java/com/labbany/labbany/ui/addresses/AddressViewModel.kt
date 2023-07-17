package com.labbany.labbany.ui.addresses

import android.content.Context
import androidx.lifecycle.ViewModel
import com.labbany.labbany.R
import com.labbany.labbany.data.AddressesServices
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.pojo.model.SimpleModel
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddressViewModel(private val addressesServices: AddressesServices) : ViewModel() {

    private val _citiesStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val citiesStateFlow: MutableStateFlow<NetworkState> get() = _citiesStateFlow

    private val _addAddressStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val addAddressStateFlow: MutableStateFlow<NetworkState> get() = _addAddressStateFlow

    private val _updateAddressStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val updateAddressStateFlow: MutableStateFlow<NetworkState> get() = _updateAddressStateFlow

    private val _deleteAddressStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val deleteAddressStateFlow: MutableStateFlow<NetworkState> get() = _deleteAddressStateFlow

    private val _addressesStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val addressesStateFlow: MutableStateFlow<NetworkState> get() = _addressesStateFlow

    fun counties(mContext: Context): ArrayList<SimpleModel> = arrayListOf(
        SimpleModel(
            0, mContext.getString(
                R.string.ksa
            )
        )
    )

    fun cities() {

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

    fun addAddress(
        user_id: Int,
        location: String,
        district_name: String,
        city_id: Int,
        receiver_name: String,
        receiver_phone: String,
        latitude: Double,
        longitude: Double
    ) {

        _addAddressStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.addAddress(
                    user_id = user_id,
                    location = Utils.requestBody(location),
                    district_name = Utils.requestBody(district_name),
                    city_id = city_id,
                    receiver_name = Utils.requestBody(receiver_name),
                    receiver_phone = Utils.requestBody(receiver_phone),
                    latitude = latitude,
                    longitude = longitude
                )
            }.onFailure {
                _addAddressStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _addAddressStateFlow.value = NetworkState.Result(it.body())
                else
                    _addAddressStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun updateAddress(
        user_id: Int, address_id: Int,
        location: String,
        district_name: String,
        city_id: Int,
        receiver_name: String,
        receiver_phone: String,
        latitude: Double,
        longitude: Double
    ) {

        _updateAddressStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.updateAddress(
                    address_id = address_id,
                    user_id = user_id,
                    location = Utils.requestBody(location),
                    district_name = Utils.requestBody(district_name),
                    city_id = city_id,
                    receiver_name = Utils.requestBody(receiver_name),
                    receiver_phone = Utils.requestBody(receiver_phone),
                    latitude = latitude,
                    longitude = longitude
                )
            }.onFailure {
                _updateAddressStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _updateAddressStateFlow.value = NetworkState.Result(it.body())
                else
                    _updateAddressStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun deleteAddress(
        user_id: Int, address_id: Int
    ) {

        _deleteAddressStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.deleteAddress(address_id = address_id, user_id = user_id)
            }.onFailure {
                _deleteAddressStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _deleteAddressStateFlow.value = NetworkState.Result(it.body())
                else
                    _deleteAddressStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun address(
        user_id: Int
    ) {

        _addressesStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                addressesServices.address(
                    user_id
                )
            }.onFailure {
                _addressesStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _addressesStateFlow.value = NetworkState.Result(it.body())
                else
                    _addressesStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}