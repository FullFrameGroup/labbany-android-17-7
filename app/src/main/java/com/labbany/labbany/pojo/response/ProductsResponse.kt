package com.labbany.labbany.pojo.response

import com.labbany.labbany.pojo.model.Banner
import com.labbany.labbany.pojo.model.Category

data class ProductsResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {

    data class Data(
        val banners: List<Banner>?,
        val categories: List<Category>?
    )

}