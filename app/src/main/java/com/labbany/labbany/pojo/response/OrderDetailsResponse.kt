package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.Order

data class OrderDetailsResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val order: List<Order>
    )
}