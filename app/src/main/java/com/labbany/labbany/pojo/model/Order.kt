package com.labbany.labbany.pojo.model

data class Order(
    val address: Address,
    val additional_discount: String,
    val campaign_code: String,
    val campaign_type: String,
    val campaign_value: String,
    var discount: Double=0.0,
    val is_feedback_given: String,
    val mem_id: Int,
    val order_status_id: String,
    val cart_id: Int,
    val cart_item: List<CartItem>,
    val date: String,
    val iscancel: String,
    val o_id: Int,
    val order_status: String,
    val order_status_str: String,
    val payment_type: String,
    val shipping: Double,
    val tax: Double,
    val time: String,
    val total: Double,
    val wallet_amount: Double
) {
    data class Address(
        val alter_contact: String,
        val alter_name: String,
        val city_arabic: String,
        val city_english: String,
        val city_id: Int,
        val city_name: String,
        val district_name: String,
        val latitude: Double,
        val location: String,
        val longitude: Double,
        val shipping_id: Int,
        val title: String?,
        val user_id: Int
    )

    data class CartItem(
        val cart_id: Int,
        val cart_item_id: Int,
        val cut_id: Int?,
        val cut_name: String,
        val cut_price: Double,
        val extra_id: Int?,
        val extra_name: String,
        val extra_price: Double,
        val notes: String?,
        val packing_id: Int?,
        val packing_name: String,
        val packing_price: Double,
        val price_after_discount: Int?,
        val product_id: Int,
        val product_image: String?,
        val product_name: String,
        val product_type: String,
        val qty: Int,
        val size_id: Int?,
        val size_name: String,
        val size_price: Double,
        val total_price: Double
    )
}