package com.food.ordering.zinger.data.retrofit

import android.content.Context
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) :
        Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
                "/user/customer"
        )
        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
            println("oauth_id testing 1"+preferences.oauthId)
            req.newBuilder()
                    .addHeader("oauth_id", preferences.oauthId)
                    .addHeader("id", preferences.userId.toString())
                    .addHeader("role", preferences.role)
                    .build()
        } else {
            println("oauth_id testing 2"+preferences.oauthId)
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        return response
    }
}