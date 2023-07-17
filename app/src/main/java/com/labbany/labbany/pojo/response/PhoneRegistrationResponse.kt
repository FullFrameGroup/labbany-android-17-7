package com.labbany.labbany.pojo.response

data class PhoneRegistrationResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    class Data
}