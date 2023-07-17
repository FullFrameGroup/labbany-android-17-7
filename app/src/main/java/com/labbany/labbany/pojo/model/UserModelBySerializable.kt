package com.labbany.labbany.pojo.model

import java.io.Serializable

data class UserModelBySerializable(
    val city_id: Int,
    val city_name: String,
    val email: String,
    val fcm_token: String?,
    val id: Int,
    val image: String?,
    val username: String,
    val phone: String,
    val wallet: Double = 0.0,//not required at all apis
    val token: String?
):Serializable