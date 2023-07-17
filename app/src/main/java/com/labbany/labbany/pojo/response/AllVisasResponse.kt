package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.VisaModel

data class AllVisasResponse(
    val code: Int,
    val `data`: List<VisaModel>,
    val msg: String,
    val success: Boolean
)