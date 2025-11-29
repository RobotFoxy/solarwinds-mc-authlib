package net.ccbluex.liquidbounce.authlib.utils

import net.ccbluex.liquidbounce.authlib.Authlib
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request.Builder
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * A utility class for making HTTP requests.
 */
internal object HttpUtils {

    /**
     * Make an HTTP request.
     *
     * @param url URL to connect to
     * @param method HTTP method to use
     * @param data Data to send
     * @param header HTTP header to send
     * @param agent User agent to use
     * @return The response code and response body
     */
    fun request(url: String, method: String, data: String = "", header: Map<String, String> = emptyMap()): Pair<Int, String> {
        val request = Builder()
            .url(url)
            .method(method, data.toRequestBody())
            .headers(header.toHeaders())
            .build()
        val response = Authlib.client.newCall(request).execute()

        return response.code to response.body.string()
    }

    fun get(url: String, header: Map<String, String> = emptyMap()) =
        request(url, "GET", header = header)

    fun post(url: String, data: String, header: Map<String, String> = emptyMap()) =
        request(url, "POST", data, header)

    inline fun <reified T> post(url: String, data: Any): T {
        val (_, text) = post(url, GSON.toJson(data), mapOf("Content-Type" to "application/json"))
        return decode(text)
    }
    
}
