package com.labbany.labbany.data

import com.labbany.labbany.pojo.response.*
import com.labbany.labbany.util.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthServices {

    @Multipart
    @POST("signup_new.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun register(
        @Part("username") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("confirm_password") confirm_password: RequestBody,
        @Part("device_token") fcm_token: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part("device_type") device_type: RequestBody?,
        @Part("os_version") app_version: RequestBody?
    ): Response<RegistrationResponse>

    @Multipart
    @POST("phone_registration.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun phoneRegistration(
        @Part("id") id: Int,
        @Part("phone") phone: RequestBody
    ): Response<PhoneRegistrationResponse>

    @Multipart
    @POST("resend_otp.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun sendOrResendOtp(
        @Part("id") id: Int,
        @Part("phone") phone: RequestBody
    ): Response<OtpResponse>

    @Multipart
    @POST("otpVerify.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun codeCheck(
        @Part("id") id: Int,
        @Part("otp") otp: RequestBody,
        @Part("is_registeration") is_registration: Int =Constants.REGISTRATION_PARAMS//1 if registration and 0 if forget password
    ): Response<CheckCodeResponse>

    @Multipart
    @POST("login.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun login(
        @Part("userDetail") userDetail: RequestBody,
        @Part("password") password: RequestBody,
        @Part("device_token") fcm_token: RequestBody?,
        @Part("device_type") device_type: RequestBody?,
        @Part("os_version") app_version: RequestBody?
    ): Response<LoginResponse>

    @Multipart
    @POST("forget_password.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun forgePassword(
        @Part("phone") phone: RequestBody
    ): Response<RegistrationResponse>

    @Multipart
    @POST("reset_password.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun resetPassword(
        @Part("phone") phone: RequestBody,
        @Part("new_password") new_password: RequestBody
    ): Response<GeneralResponse>

    @Multipart
    @POST("change_password.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun changePassword(
        @Part("id") id: Int,
        @Part("old_password") old_password: RequestBody,
        @Part("new_password") new_password: RequestBody
    ): Response<GeneralResponse>

    @Multipart
    @POST("profile.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun profile(
        @Part("id") id: Int
    ): Response<ProfileResponse>

    @Multipart
    @POST("update_profile.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun updateProfile(
        @Part("id") id: Int,
        @Part("username") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ProfileResponse>

    @Multipart
    @POST("logout.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun logout(
        @Part("id") id: Int
    ): Response<GeneralResponse>

    @Multipart
    @POST("social_login.php")
    @Headers("x-api-key:${Constants.API_GENERAL_AUTH}")
    suspend fun social(
        @Part("userDetail") userDetail: RequestBody,
        @Part("device_token") fcm_token: RequestBody?,
        @Part("device_type") device_type: RequestBody?,
        @Part("os_version") app_version: RequestBody?
    ): Response<CheckCodeResponse>

}