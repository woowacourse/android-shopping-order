package woowacourse.shopping.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ApiClient() {
    private val client = OkHttpClient()
    private val requestBuilder: Request.Builder =
        Request.Builder().addHeader("Content-Type", "application/json")
    fun getApiService(path: String, header: String? = null): Response {
        val request = requestBuilder
            .url(baseUrl + path)
            .header("Authorization", "Basic $header")
            .build()
        return client.newCall(request).execute()
    }

    fun deleteApiService(path: String, header: String? = null): Response {
        val request = requestBuilder
            .url(baseUrl + path)
            .header("Authorization", "Basic $header")
            .delete()
            .build()
        return client.newCall(request).execute()
    }

    fun postApiService(header: String, body: String, path: String? = null): Response {

        val request = requestBuilder
            .url(baseUrl + path)
            .header("Authorization", "Basic $header")
            .post(body.toRequestBody())
            .build()
        return client.newCall(request).execute()
    }

    fun patchApiService(header: String, body: String, path: String? = null): Response {
        val request = requestBuilder
            .url(baseUrl + path)
            .header("Authorization", "Basic $header")
            .patch(body.toRequestBody())
            .build()
        return client.newCall(request).execute()
    }

    fun getRequestBody(name: String, value: String): String {
        return "{\"$name\": \"$value\"}"
    }

    companion object {
        var baseUrl = ""
    }
}
