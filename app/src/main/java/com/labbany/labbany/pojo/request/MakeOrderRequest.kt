package com.labbany.labbany.pojo.request

data class MakeOrderRequest(
    val additional_discount: String?,
    val campaign_code: String?,
    val campaign_id: String?,
    val campaign_type: String?,
    val campaign_value: String?,
    val cart_id: Int,
    val cart_items: List<CartItem>,
    val coupon_code: String?,
    val coupon_id: Int?,
    val date: String?,
    val discount: Double?,
    val payment_type: String,
    val sd_amount: String,
    val ship_charge: Double,
    val ship_id: Int,
    val tax: Double,
    val time: String?,
    val total_payment: Double,
    val user_id: Int
) {

    data class CartItem(
        val id: Int,
        val qty: Int,
        val total_price: Double,
        val unit_price: Double
    )
}