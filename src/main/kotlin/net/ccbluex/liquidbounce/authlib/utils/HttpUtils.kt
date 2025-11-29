package net.ccbluex.liquidbounce.authlib.utils

import com.google.gson.Gson
import com.google.gson.JsonElement
import net.ccbluex.liquidbounce.authlib.Authlib
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSink

inline operator fun Headers.plus(other: Headers): Headers = this.newBuilder().addAll(other).build()

private fun Buffer.asRequestBody(mediaType: MediaType) = object : RequestBody() {
    override fun contentType() = mediaType
    override fun contentLength(): Long = size
    override fun writeTo(sink: BufferedSink) {
        sink.writeAll(this@asRequestBody.copy())
    }
}

/**
 * Creates request body from JSON.
 */
@JvmOverloads
fun JsonElement.toRequestBody(gson: Gson = GSON): RequestBody {
    val buffer = Buffer()
    buffer.outputStream().writer(Charsets.UTF_8).use {
        gson.toJson(this, it)
    }
    return buffer.asRequestBody(HttpUtils.MEDIA_TYPE_JSON)
}

fun Gson.makeRequestBody(data: Any?): RequestBody {
    val buffer = Buffer()
    buffer.outputStream().writer(Charsets.UTF_8).use {
        toJson(data, it)
    }
    return buffer.asRequestBody(HttpUtils.MEDIA_TYPE_JSON)
}

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

    @JvmField
    internal val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    /**
     * Make an HTTP request.
     *
     * @param url URL to connect to
     * @param method HTTP method to use
     * @param body Data to send
     * @param headers HTTP header to send
     * @return The response code and response body
     */
    private fun request(url: String, method: String, body: RequestBody?, headers: Headers): Pair<Int, String> {
        val request = Request.Builder()
            .url(url)
            .method(method, body)
            .headers(headers)
            .build()
        val response = Authlib.client.newCall(request).execute()

        return response.code to response.body.string()
    }

    fun get(url: String, headers: Headers = Headers.EMPTY) =
        request(url, "GET", body = null, headers = headers)

    fun post(url: String, data: String, headers: Headers = Headers.EMPTY) =
        post(url, data.toRequestBody(), headers)

    fun post(url: String, data: RequestBody, headers: Headers = Headers.EMPTY) =
        request(url, "POST", data, headers)

    inline fun <reified T> post(url: String, data: Any): T {
        val (_, text) = post(url, GSON.makeRequestBody(data), HEADERS_JSON)
        return decode(text)
    }

}
