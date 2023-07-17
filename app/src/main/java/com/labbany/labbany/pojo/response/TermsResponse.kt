package com.labbany.labbany.pojo.response

data class TermsResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val content: String
    )
}