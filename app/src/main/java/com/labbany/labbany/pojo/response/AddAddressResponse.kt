package com.labbany.labbany.pojo.response

data class AddAddressResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val shipping_id: Int
    )
}