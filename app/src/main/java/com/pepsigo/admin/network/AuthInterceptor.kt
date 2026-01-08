package com.pepsigo.admin.network

import com.pepsigo.admin.utils.AuthEventBus
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            // 🔥 session expired
            AuthEventBus.sendLogout()
        }
        return response
    }
}