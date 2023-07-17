package com.labbany.labbany.pojo.model

data class VisaModel(
    val card_number: String,
    val card_holder_name: String,
    val cart_date: String,
    val secret_code: String,
    val visa_type_img:String?,
    val visa_id: Int,
    val visa_type: String
)