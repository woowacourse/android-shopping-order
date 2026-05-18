package woowacourse.shopping.data.remote.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.data.remote.mock.dto.MockProductResponse
import woowacourse.shopping.data.remote.mock.dto.toObject
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product

class MockProductRepositoryImpl(
    private val client: OkHttpClient,
    private val baseUrl: String,
) : ProductRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): List<Product> =
        withContext(Dispatchers.IO) {
            val offset = page * pageSize
            val url = "${baseUrl}products?offset=$offset&limit=$pageSize"

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw okio.IOException("Unexpected code $response")

                val responseBody = response.body.string() ?: ""
                val webResponse = json.decodeFromString<List<MockProductResponse>>(responseBody)
                webResponse.map { it.toObject() }
            }
        }

    override suspend fun getProduct(id: Long): Product =
        withContext(Dispatchers.IO) {
            val url = "${baseUrl}products/$id"

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw okio.IOException("Unexpected code $response")

                val responseBody = response.body.string() ?: ""
                val webResponse = json.decodeFromString<MockProductResponse>(responseBody)
                webResponse.toObject()
            }
        }

    override suspend fun getCategoryProducts(
        page: Int,
        pageSize: Int,
        category: String,
    ): List<Product> {
        val offset = page * pageSize
        val url = "${baseUrl}products?offset=$offset&limit=$pageSize"

        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw okio.IOException("Unexpected code $response")

            val responseBody = response.body.string() ?: ""
            val webResponse = json.decodeFromString<List<MockProductResponse>>(responseBody)
            webResponse.map { it.toObject() }.filter { it.category == category }
        }
    }
}
