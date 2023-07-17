package com.labbany.labbany.pojo.response

import java.io.Serializable

data class AddressesResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val shipping: List<Address>
    )

    data class Address(
        val alter_contact: String,
        val alter_name: String,
        val city: String,
        val city_arabic: String,
        val city_id: Int,
        val city_name: String,
        val district_name: String,
        val is_selected: String,
        val latitude: Double?,
        val location: String,
        val longitude: Double?,
        val mem_id: Int,
        val shipping_charge: String,
        val shipping_id: Int
    ):Serializable
}