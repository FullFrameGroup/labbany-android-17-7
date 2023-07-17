package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.CheckoutIdResponse
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import com.labbany.labbany.util.hyperpay.HyperUtils
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface HyperPayServices {

    @Multipart
    @POST("paymenthypnew.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun requestCheckoutId(
        @Part("merchant_customerId") customerId: Int,
        @Part("customer_email") customer_email: RequestBody,
        @Part("billing_city") billing_city: RequestBody,
        @Part("amount") amount: Double,
        @Part("merchantTransactionId") merchantTransactionId: RequestBody?,//cartId ="cart" + cartID
        @Part("merchantInvoiceId") merchantInvoiceId: RequestBody,//cartId ="cart" + cartID
        @Part("given_name") given_name: RequestBody,//of card holder name
        @Part("payment_method") payment_method: RequestBody,
        @Part("resourcePath") resourcePath: RequestBody = Utils.requestBody(""),
        @Part("billing_postcode") billing_postcode: RequestBody = Utils.requestBody("000"),
        @Part("billing_state") billing_state: RequestBody = Utils.requestBody("1"),
        @Part("surname") surname: RequestBody = Utils.requestBody(""),
        @Part("billing_street2") billing_street2: RequestBody = Utils.requestBody("123"),
        @Part("billing_street1") billing_street1: RequestBody = Utils.requestBody("123"),
        @Part("billing_country") billing_country: RequestBody = Utils.requestBody("SA"),
        @Part("method") method: RequestBody = Utils.requestBody("payment"),
//        @Part("payment_mode") payment_mode: RequestBody = Utils.requestBody(HyperUtils.PaymentMode.Development.value)
    ): Response<CheckoutIdResponse>

    @Multipart
    @POST("paymenthypnew.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun requestPaymentStatus(
        @Part("resourcePath") resourcePath: RequestBody,
        @Part("payment_method") payment_method: RequestBody,
        @Part("method") method: RequestBody = Utils.requestBody("check_payment"),
//        @Part("payment_mode") payment_mode: RequestBody = Utils.requestBody(HyperUtils.PaymentMode.Development.value)
    ): Response<JsonObject>

}