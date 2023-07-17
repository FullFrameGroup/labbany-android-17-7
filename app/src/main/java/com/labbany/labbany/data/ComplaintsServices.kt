package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.util.Constants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ComplaintsServices {

    @POST("get_complaint_type.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun complaintTypes(
    ): Response<ComplaintTypesResponse>

    @GET("compliants.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun complaints(
        @Query("user_id") user_id: Int
    ): Response<ComplaintsResponse>

    @Multipart
    @POST("upload_complaint.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun uploadComplaint(
        @Part("user_id") user_id: Int,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("title") complaint_type_id: Int,
        @Part("description") description: RequestBody
    ): Response<GeneralResponse>

}