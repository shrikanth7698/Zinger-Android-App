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
        /*//Check if device is connected to internet
        if (!NetworkUtils.isOnline(context)) {
            throw NoConnectivityException()z
        }*/

        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
            println("oauth_id testing 1"+preferences.oauthId)
            req.newBuilder()
                    .addHeader("oauth_id", preferences.oauthId)
                    .addHeader("mobile", preferences.mobile)
                    .addHeader("role", preferences.role)
                    .build()
        } else {
            println("oauth_id testing 2"+preferences.oauthId)
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        //Check for UnAuthenticated Request
        /*if (response.code() == HTTP_UNAUTHORIZED) {
            if(whiteListedEndpoints.contains(req.url().encodedPath())){
                throw InvalidCredentialsException()
            }else {
                (context as NiaClubApp).onCustomAppAuthFailed()
                throw CustomAppUnAuthorizedException()
            }
        }*/
        return response
    }
}