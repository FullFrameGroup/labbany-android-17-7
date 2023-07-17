package com.labbany.labbany.ui.product_details

import android.content.Context
import androidx.lifecycle.ViewModel
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.pojo.model.CartModel
import com.labbany.labbany.pojo.model.PriceModel
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _productDetailsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val productDetailsStateFlow: MutableStateFlow<NetworkState> get() = _productDetailsStateFlow

    private val _addToCartStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val addToCartStateFlow: MutableStateFlow<NetworkState> get() = _addToCartStateFlow

    fun productDetails(cityId: Int, productId: Int) {
        _productDetailsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.productDetails(cityId, productId)
            }.onFailure {
                _productDetailsStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _productDetailsStateFlow.value = NetworkState.Result(it.body())
                else
                    _productDetailsStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun addToCart(userID: Int, cartModel: CartModel) {
        _addToCartStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.addToCart(userID, cartModel)
            }.onFailure {
                _addToCartStateFlow.value = NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _addToCartStateFlow.value = NetworkState.Result(it.body())
                else
                    _addToCartStateFlow.value = NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun coins(mContext: Context): ArrayList<PriceModel> =
        arrayListOf(PriceModel(0, mContext.getString(R.string.sar), 0f))

    companion object {
//        private val TAG = this::class.java.name
    }

}