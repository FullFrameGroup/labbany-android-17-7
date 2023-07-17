package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.CityModel

data class CitiesResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val cities: List<CityModel>,
        val tax_vat: String
    )

}