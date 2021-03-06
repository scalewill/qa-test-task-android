package com.flatstack.qatesttask.data.guardiannews.retrofit

import com.flatstack.qatesttask.BuildConfig.THE_GUARDIAN_BASE_URL
import com.flatstack.qatesttask.data.guardiannews.extentions.addQueriesToInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val THE_GUARDIAN_TIMEOUT = 30L

class GuardianHttpService(private val apiKey: String) {

    fun getPageSizeInterceptor() = Interceptor {
        it.addQueriesToInterceptor(
            "page-size" to "10"
        )
    }
    fun getBearerAuthorizationInterceptor() = Interceptor {
        it.addQueriesToInterceptor(
            "api-key" to apiKey
        )
    }
    fun getEncodingInterceptor() = Interceptor {
        val request = it.request()
        val req = request.url.toString().replace("%26", "&")
        it.proceed(Request.Builder().url(req).build())
    }
    fun getFormatInterceptor() = Interceptor {
        it.addQueriesToInterceptor(
            "format" to "json"
        )
    }
    fun getThumbnailInterceptor() = Interceptor {
        it.addQueriesToInterceptor(
            "show-fields" to "thumbnail"
        )
    }
    fun getClient(
        interceptors: Collection<Interceptor>
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(THE_GUARDIAN_TIMEOUT, TimeUnit.SECONDS).apply {
            for (interceptor in interceptors) {
                addInterceptor(interceptor)
            }
        }
        .build()
    fun getBaseRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(THE_GUARDIAN_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun getService(retrofit: Retrofit): GuardianService =
        retrofit.create(
            GuardianService::class.java
        )
}
