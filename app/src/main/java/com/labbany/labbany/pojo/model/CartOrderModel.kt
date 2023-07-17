package com.labbany.labbany.pojo.model

import java.io.Serializable

data class CartOrderModel(
    val cart_item_id: Int,
    val cut_id: Int?,
    val cut_name: String?,
    val cut_price: Double?,
    val extra_id: Int?,
    val extra_name: String?,
    val extra_price: Double?,
    val notes: String?,
    val packing_id: Int?,
    val packing_name: String?,
    val packing_price: Double?,
    val product_id: Int,
    val product_image: String?,
    val product_name: String,
    val product_type: Int,
    var qty: Int,
    var qty_price: Double,
    val size_id: Int?,
    val size_name: String?,
    val size_price: Double?,
    var total_price: Double,
    var total_price_with_discount: Double
):Serializable