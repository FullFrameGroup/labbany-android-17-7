package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.UserModel

data class LoginResponse(
    val code: Int,
    val `data`: UserModel?=null,
    val msg: String,
    val success: Boolean=false
)