package com.labbany.labbany.pojo.response

data class RegistrationResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val id: Int
    )
}