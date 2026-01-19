package com.pepsigo.admin.network

import com.pepsigo.admin.data.TokenProvider
import com.pepsigo.admin.data.UserPreferenceRepository
import com.pepsigo.admin.utils.AuthEventBus
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val path = chain.request().url.encodedPath
        val isAuthEndpoint = path.startsWith("/api/login")
        val token = tokenProvider.hasToken()

        if (response.code == 401 && token && !isAuthEndpoint) {
            // 🔥 session expired
            AuthEventBus.sendLogout()
        }
        return response
    }
}