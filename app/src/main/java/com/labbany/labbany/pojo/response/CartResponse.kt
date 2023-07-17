package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.CartOrderModel
import com.labbany.labbany.pojo.model.VisaTypeModel

data class CartResponse(
    val code: Int,
    val `data`: Data? = null,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val cart_id: Int,
        val cart_items: List<CartOrderModel>,
        val visa_types: List<VisaTypeModel>,
        val wallet_amount: Double = 0.0,
        val shipping: Double?,
        val tax: Double?,
        val date_setting: DateSetting
    )

    data class DateSetting(
        val avaliable_days: Int,
        val `open`: String
    )
}