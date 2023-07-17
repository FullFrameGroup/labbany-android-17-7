package com.labbany.labbany.pojo.response

data class WalletResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val wallet_amount: Double,
        val wallet_list: List<Wallet>
    )

    data class Wallet(
        val amount: Double,
        val date_time: String,
        val description: String
    )
}