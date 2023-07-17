package com.labbany.labbany.pojo.response

data class AddCartResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {

    data class Data(
        val cart_id: String
    )
}