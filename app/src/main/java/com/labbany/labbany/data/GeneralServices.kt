package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.NotificationsResponse
import com.labbany.labbany.pojo.response.TermsResponse
import com.labbany.labbany.util.Constants
import retrofit2.Response
import retrofit2.http.*

interface GeneralServices {

    @GET("terms.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun terms(): Response<TermsResponse>

    @Multipart
    @POST("get_notification.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun notifications(
        @Part("user_id") user_id: Int,
    ): Response<NotificationsResponse>

}