package net.ccbluex.liquidbounce.authlib

import net.ccbluex.liquidbounce.authlib.interceptor.DefaultHeaderInterceptor
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.Executors

object Authlib {

    private const val DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36"

    @Volatile
    private var customClient: OkHttpClient? = null
    @Volatile
    private var defaultClient: OkHttpClient? = null

    var client: OkHttpClient
        get() {
            customClient?.let { return it }

            val existing = defaultClient
            if (existing != null) return existing

            return synchronized(this) {
                defaultClient ?: createDefaultClient().also {
                    defaultClient = it
                }
            }
        }
        set(value) {
            customClient = value
        }

    private fun createDefaultClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .dispatcher(Dispatcher(Executors.newVirtualThreadPerTaskExecutor()))
            .addInterceptor(DefaultHeaderInterceptor("User-Agent", DEFAULT_AGENT))
            .build()
    }

}