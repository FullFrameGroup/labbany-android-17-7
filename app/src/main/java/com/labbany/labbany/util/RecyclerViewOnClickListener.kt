package com.labbany.labbany.util

import com.labbany.labbany.pojo.model.CartOrderModel
import com.labbany.labbany.pojo.model.Order
import com.labbany.labbany.pojo.model.VisaModel
import com.labbany.labbany.pojo.response.AddressesResponse
import com.labbany.labbany.ui.addresses.AddressAdapter
import com.labbany.labbany.ui.payment.PaymentAdapter

interface RecyclerViewOnClickListener {

    fun <UI, D> onRootClickListener(dataBinding: UI, data: D) {}

    fun <UI, D> onOfferClickListener(dataBinding: UI, data: D) {}

    fun onTotalPriceChanged(totalOld: Double, totalNew: Double) {}

    fun onRemoveVisa(visaModel: VisaModel, viewHolder: PaymentAdapter.ViewHolder, position: Int) {}

//    fun onVisaClickListener(visaModel: VisaModel) {}

    fun onRemoveAddressClickListener(
        addressModel: AddressesResponse.Address,
        viewHolder: AddressAdapter.ViewHolder,
        position: Int
    ) {
    }

    fun onAddressClickListener(addressModel: AddressesResponse.Address) {}

    fun onUpdateAddressClickListener(addressModel: AddressesResponse.Address) {}

    fun onDeleteCartItemClickListener(cartOrderModel: CartOrderModel,position: Int) {}

    fun onCancelOrderClickListener(order: Order,position: Int) {}

    fun onOrderClickListener(orderId: Int) {}

}