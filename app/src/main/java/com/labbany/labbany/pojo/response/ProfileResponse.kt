package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.UserModel

data class ProfileResponse(
    val code: Int,
    val `data`: UserModel?,
    val msg: String,
    val success: Boolean
)