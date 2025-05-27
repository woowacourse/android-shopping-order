package woowacourse.shopping.data.service

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.data.mockserver.MockServer
import woowacourse.shopping.data.model.PageableResponse
import woowacourse.shopping.data.model.ProductResponse

class MockProductService(
    private val client: OkHttpClient,
    private val server: MockServer,
) : ProductService {
    override fun findProductById(id: Long): ProductResponse {
        val url = "/product/$id"
        return executeRequest<ProductResponse>(url)
    }

    override fun findProductsByIds(ids: List<Long>): List<ProductResponse> {
        val url = "/product?ids=${Json.encodeToString(ids)}"
        return executeRequest<List<ProductResponse>>(url)
    }

    override fun loadProducts(
        offset: Int,
        limit: Int,
    ): PageableResponse<ProductResponse> {
        val url = "/products/?offset=$offset&limit=$limit"
        return executeRequest<PageableResponse<ProductResponse>>(url)
    }

    private inline fun <reified T> executeRequest(url: String): T {
        val httpUrl = server.createUrlWithBase(url)
        val request = Request.Builder().url(httpUrl).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IllegalArgumentException(
                FAILURE_MESSAGE.format(response.code, httpUrl),
            )
        }

        val responseBody = response.body?.string()
        if (responseBody.isNullOrEmpty()) {
            throw IllegalStateException(
                EMPTY_RESPONSE_MESSAGE.format(httpUrl),
            )
        }

        return Json.decodeFromString<T>(responseBody)
    }

    companion object {
        private const val FAILURE_MESSAGE = "Request failed: HTTP %d for URL %s"
        private const val EMPTY_RESPONSE_MESSAGE = "Empty response body for URL %s"
    }
}
