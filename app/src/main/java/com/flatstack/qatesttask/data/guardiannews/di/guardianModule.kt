package com.flatstack.qatesttask.data.guardiannews

import com.flatstack.qatesttask.BuildConfig
import com.flatstack.qatesttask.data.guardiannews.retrofit.GuardianHttpService
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module

val guardianModule = module {
    single {
        GuardianHttpService(BuildConfig.API_KEY)
    }
    single {
        get<GuardianHttpService>().getService(
            get<GuardianHttpService>().getBaseRetrofit(get())
        )
    }
    factory(named("pageSizeInterceptor")) {
        get<GuardianHttpService>().getPageSizeInterceptor()
    }
    factory(named("bearerAuthorizationInterceptor")) {
        get<GuardianHttpService>().getBearerAuthorizationInterceptor()
    }
    factory {
        get<GuardianHttpService>().getClient(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            },
            get(named("pageSizeInterceptor")),
            get(named("bearerAuthorizationInterceptor")),
        )
    }
}