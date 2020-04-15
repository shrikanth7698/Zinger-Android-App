package com.food.ordering.zinger.di

import com.food.ordering.zinger.BuildConfig
import com.food.ordering.zinger.data.retrofit.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { AuthInterceptor(get(),get()) }
    single { provideRetrofit(get()) }
    single { UserRepository(get()) }
    single { ShopRepository(get()) }
    single { PlaceRepository(get()) }
    single { OrderRepository(get()) }
}

fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.CUSTOM_BASE_URL).client(provideOkHttpClient(authInterceptor))
            .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    val builder = OkHttpClient()
            .newBuilder()
            .addInterceptor(authInterceptor)

    if (BuildConfig.DEBUG) {
        val requestInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addNetworkInterceptor(requestInterceptor)
    }
    return builder.build()
}