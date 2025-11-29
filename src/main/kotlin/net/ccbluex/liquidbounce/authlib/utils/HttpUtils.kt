package net.ccbluex.liquidbounce.authlib.utils

import net.ccbluex.liquidbounce.authlib.Authlib
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

inline operator fun Headers.plus(other: Headers): Headers = this.newBuilder().addAll(other).build()

/**
 * A utility class for making HTTP requests.
 */
internal object HttpUtils {

    @JvmField
    internal val HEADERS_JSON = headersOf("Content-Type", "application/json")

    @JvmField
    internal val HEADERS_FORM = headersOf("Content-Type", "application/x-www-form-urlencoded")

    @JvmField
    internal val HEADERS_JSON_RESPONSE = headersOf("Accept", "application/json")

    /**
     * Make an HTTP request.
     *
     * @param url URL to connect to
     * @param method HTTP method to use
     * @param data Data to send
     * @param headers HTTP header to send
     * @return The response code and response body
     */
    private fun request(url: String, method: String, data: String = "", headers: Headers): Pair<Int, String> {
        val request = Request.Builder()
            .url(url)
            .method(method, data.toRequestBody())
            .headers(headers)
            .build()
        val response = Authlib.client.newCall(request).execute()

        return response.code to response.body.string()
    }

    fun get(url: String, headers: Headers = Headers.EMPTY) =
        request(url, "GET", headers = headers)

    fun post(url: String, data: String, headers: Headers = Headers.EMPTY) =
        request(url, "POST", data, headers)

    inline fun <reified T> post(url: String, data: Any): T {
        val (_, text) = post(url, GSON.toJson(data), HEADERS_JSON)
        return decode(text)
    }
    
}
