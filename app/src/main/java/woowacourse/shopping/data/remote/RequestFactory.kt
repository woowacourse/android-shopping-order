package woowacourse.shopping.data.remote

import okhttp3.Request
import okhttp3.RequestBody

class RequestFactory(private val baseUrl: String) {
    private fun getDefaultRequestBuilder(path: String) = Request.Builder()
        .url(baseUrl + path)
        .addHeader(AUTH_KEY, USER_EMAIL_INFO)

    fun getGetRequest(path: String): Request {
        return getDefaultRequestBuilder(path).build()
    }

    fun getPostRequest(path: String, body: RequestBody? = null): Request {
        if (body == null) {
            return getDefaultRequestBuilder(path).build()
        }
        return getDefaultRequestBuilder(path).post(body).build()
    }

    fun getPatchRequest(path: String, body: RequestBody? = null): Request {
        if (body == null) {
            return getDefaultRequestBuilder(path).build()
        }
        return getDefaultRequestBuilder(path).patch(body).build()
    }

    companion object {
        private const val AUTH_KEY = "Authorization"
        private const val USER_EMAIL_INFO = "Basic YUBhLmNvbToxMjM0"
    }
}
