package com.labbany.labbany.pojo.model

import java.io.Serializable

data class SimpleLocationModel(
    val district_name: String,
    val latitude: Double,
    val longitude: Double,
    val location: String
):Serializable
