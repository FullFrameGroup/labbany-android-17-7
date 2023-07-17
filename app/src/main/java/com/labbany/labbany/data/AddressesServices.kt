package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.util.Constants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AddressesServices {

    @Multipart
    @POST("set_city.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun setCity(
        @Part("id") id:Int,
        @Part("city_id") city_id:Int
    ): Response<GeneralResponse>

    @GET("city_list.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun cities(): Response<CitiesResponse>


    @Multipart
    @POST("add_shipping_address.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun addAddress(
        @Part("user_id") user_id: Int,
        @Part("location") location: RequestBody,
        @Part("district_name") district_name: RequestBody,
        @Part("city") city_id: Int,
        @Part("alter_name") receiver_name: RequestBody,
        @Part("alter_contact") receiver_phone: RequestBody,
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double
    ): Response<AddAddressResponse>

    @Multipart
    @POST("update_shipping_address.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun updateAddress(
        @Part("shipping_id") address_id: Int,
        @Part("user_id") user_id: Int,
        @Part("location") location: RequestBody,
        @Part("district_name") district_name: RequestBody,
        @Part("city") city_id: Int,
        @Part("alter_name") receiver_name: RequestBody,
        @Part("alter_contact") receiver_phone: RequestBody,
        @Part("latitude") latitude: Double,
        @Part("longitude") longitude: Double
    ): Response<GeneralResponse>

    @Multipart
    @POST("delete_shipping_address.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun deleteAddress(
        @Part("shipping_id") address_id: Int,
        @Part("user_id") user_id: Int
    ): Response<GeneralResponse>

    @Multipart
    @POST("get_shipping_list.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun address(
        @Part("user_id") user_id: Int
    ): Response<AddressesResponse>

}