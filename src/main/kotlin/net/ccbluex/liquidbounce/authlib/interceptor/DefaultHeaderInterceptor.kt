package net.ccbluex.liquidbounce.authlib.interceptor

import okhttp3.Interceptor

class DefaultHeaderInterceptor @JvmOverloads constructor(
    val key: String,
    val value: String,
    val skipIfExists: Boolean = true,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        if (skipIfExists && request.header(key) != null) {
            return chain.proceed(request)
        }
        val newRequest = request.newBuilder()
            .header(key, value)
            .build()
        return chain.proceed(newRequest)
    }
}
