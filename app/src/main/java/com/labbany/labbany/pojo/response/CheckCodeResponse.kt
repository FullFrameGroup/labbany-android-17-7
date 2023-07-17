package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.UserModel

data class CheckCodeResponse(
    val code: Int,
    val `data`: UserModel?,
    val msg: String,
    val success: Boolean
)