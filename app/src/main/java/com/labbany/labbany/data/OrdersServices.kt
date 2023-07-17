package com.labbany.labbany.data

import com.labbany.labbany.pojo.model.CartModel
import com.labbany.labbany.pojo.request.MakeOrderRequest
import com.labbany.labbany.pojo.request.RateOrderRequest
import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.util.Constants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface OrdersServices {

    @Multipart
    @POST("productList.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun products(
        @Part("temp") temp: Int = 0,
        @Part("city_id") city_id: Int?
    ): Response<ProductsResponse>

    @Multipart
    @POST("product_detials.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun productDetails(
        @Part("cityId") cityId: Int,
        @Part("productId") productId: Int
    ): Response<ProductDetailsResponse>

    @POST("add_cart.php/{user_id}")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun addToCart(
        @Path("user_id") user_id: Int,
        @Body cartModel: CartModel
    ): Response<AddCartResponse>

    @Multipart
    @POST("cart_detail.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun cartDetail(
        @Part("user_id") user_id: Int
    ): Response<CartResponse>

    @Multipart
    @POST("add_payment_detail.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun paymentDetails(
        @Part("user_id") user_id: Int,
        @Part("card_amt") card_amt: Double,
        @Part("transaction_id") transaction_id: RequestBody?,
        @Part("checkoutId") checkoutId: RequestBody,
        @Part("payment_info") payment_info: RequestBody,
        @Part("card_number") card_number: RequestBody,
        @Part("card_holdername") card_holdername: RequestBody,
        @Part("card_expairdate") card_expairdate: RequestBody,
        @Part("order_id") order_id: Int
    ): Response<GeneralResponse>

    @Multipart
    @POST("cart_item_delete.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun deleteCartItem(
        @Part("user_id") user_id: Int,
        @Part("cartId") cartId: Int,
        @Part("cartItemId") cartItemId: Int
    ): Response<GeneralResponse>

    @GET("feedback_questions.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun feedbackQuestions(): Response<QuestionsResponse>

    @Multipart
    @POST("check_coupon.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun checkOrderCoupon(
        @Part("user_id") user_id: Int,
        @Part("coupon_code") coupon_code: RequestBody,
        @Part("city_id") city_id: Int
    ): Response<OrderCouponResponse>

    @POST("save_feedback_questions.php")
    @Headers(
        "x-api-key:${Constants.API_GENERAL_AUTH}",
        "Accept:application/json"
    )//Accept:application/json
    suspend fun saveFeedbackQuestions(
        @Body rateOrderRequest: RateOrderRequest
    ): Response<GeneralResponse>

    @GET("order_detail.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun orderDetails(
        @Query("user_id") user_id: Int,
        @Query("order_id") order_id: Int
    ): Response<OrderDetailsResponse>

    @GET("my_order.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun myOrders(
        @Query("user_id") user_id: Int
    ): Response<OrderDetailsResponse>

    @GET("cancel_order.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun cancelOrder(
        @Query("user_id") user_id: Int,
        @Query("order_id") order_id: Int
    ): Response<GeneralResponse>

    @POST("add_new_order.php")
    @Headers(
        "x-api-key:${Constants.API_GENERAL_AUTH}"
//        ,"Accept:application/json",
//        "Content-Type:application/json"
    )
    suspend fun makeOrder(
        @Body makeOrderRequest: MakeOrderRequest
    ): Response<MakeOrderResponse>

    @GET("time_slot.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun timeSlot(
        @Query("date") date: String
    ): Response<TimeSlotResponse>

}