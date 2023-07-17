package com.labbany.labbany.pojo.response

import com.labbany.labbany.util.Constants

data class GeneralResponse(
    val code: Int=Constants.Codes.WAITING_CODE,
    val msg: String="",
    val `data`:Data?,
    val success: Boolean=false
){
    class Data
}