package com.labbany.labbany.pojo.response

data class MakeOrderResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val order_id: Int,
        val wallet_amount: Double
    )
}