package com.labbany.labbany.di

import com.labbany.labbany.BuildConfig
import com.labbany.labbany.data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModules {
    private const val BASE_URL = "https://nokhbaapp.com/new/api_v4/"
//    private const val BASE_URL = "https://labbany.update.tharrwa.com/api_v4/"

    val modules = module {

        single {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient=OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG)
                okHttpClient.addInterceptor(logging)

            okHttpClient.build()
        }

        single {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single {
            get<Retrofit>().create(AuthServices::class.java)
        }

        single {
            get<Retrofit>().create(AddressesServices::class.java)
        }

        single {
            get<Retrofit>().create(GeneralServices::class.java)
        }

        single {
            get<Retrofit>().create(OrdersServices::class.java)
        }

        single {
            get<Retrofit>().create(PaymentServices::class.java)
        }

        single {
            get<Retrofit>().create(ComplaintsServices::class.java)
        }

        single {
            get<Retrofit>().create(HyperPayServices::class.java)
        }
    }
}