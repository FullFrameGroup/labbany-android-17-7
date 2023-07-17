package com.labbany.labbany.pojo

data class FacebookTest(
    val email: String,
    val id: String,
    val name: String,
    val picture: Picture
)

data class Picture(
    val `data`: Data
)

data class Data(
    val height: Int,
    val is_silhouette: Boolean,
    val url: String,
    val width: Int
)