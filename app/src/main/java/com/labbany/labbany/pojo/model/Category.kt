package com.labbany.labbany.pojo.model

data class Category(
    val categoryName: String,
    val id: Int,
    val products: ArrayList<ProductModel>?
)