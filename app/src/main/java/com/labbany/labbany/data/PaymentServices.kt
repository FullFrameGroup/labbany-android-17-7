package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.util.Constants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PaymentServices {

    @Multipart
    @POST("all_visa.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun allVisas(
        @Part("user_id") user_id: Int,
        @Part("visa_type_id") visa_type_id: Int?
    ): Response<AllVisasResponse>

    @Multipart
    @POST("add_visa.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun addVisa(
        @Part("user_id") user_id: Int,
        @Part("visa_type_id") visa_type_id: Int,
        @Part("Card_holder_name") card_holder_name: RequestBody,
        @Part("card_number") card_number: RequestBody,
        @Part("card_date") card_date: RequestBody,
        @Part("secret_code") secret_code: RequestBody
    ): Response<GeneralResponse>

    @Multipart
    @POST("delete_visa.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun deleteVisa(
        @Part("user_id") user_id: Int,
        @Part("visa_id") visa_id: Int?
    ): Response<GeneralResponse>

    @GET("wallet_detail.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun walletDetails(
        @Query("user_id") user_id: Int
    ): Response<WalletResponse>

    @Multipart
    @POST("check_coupon_wallet.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun checkWalletCoupon(
        @Part("user_id") user_id: Int,
        @Part("coupon_code") coupon_code: RequestBody,
        @Part("city_id") city_id: Int
    ): Response<GeneralResponse>


}