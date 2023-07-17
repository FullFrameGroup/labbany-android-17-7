package com.labbany.labbany.pojo.response

data class OrderCouponResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val Coupon: Coupon
    )

    data class Coupon(
        val CouponType: String,
        var coupon_code: String?,
        val amount: Double,
        val coupon_id: Int,
        val minimum_amount: Double,
        var percentage: Double,
        val is_amount:Boolean
    )
}