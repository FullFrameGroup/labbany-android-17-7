package com.labbany.labbany.pojo.response

data class CheckoutIdResponse(
    val checkoutId: String,
    val code: Int,
    val `data`: Data,
    val success: Boolean
) {
    data class Data(
        val amount: String,
        val billing_city: String,
        val billing_country: String,
        val billing_postcode: String?,
        val billing_state: String,
        val billing_street1: String?,
        val billing_street2: String?,
        val customer_email: String?,
        val given_name: String?,
        val merchantInvoiceId: String?,
        val merchantTransactionId: String?,
        val merchant_customerId: String?,
        val method: String,
        val redirect: String?,
        val resourcePath: String,
        val surname: String?
    )
}