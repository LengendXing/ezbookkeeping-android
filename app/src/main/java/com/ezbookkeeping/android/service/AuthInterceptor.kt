package com.ezbookkeeping.android.service

import com.ezbookkeeping.android.ui.navigation.AuthState
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val authState: AuthState) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (authState.isLoggedIn) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${authState.accessToken}")
                .build()
        } else chain.request()
        return chain.proceed(request)
    }
}
