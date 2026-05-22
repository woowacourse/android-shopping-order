package woowacourse.shopping.viewmodel.fakes.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.ProductRepository
import woowacourse.shopping.domain.Product
import woowacourse.shopping.viewmodel.fakes.server.dto.MockProductResponse
import woowacourse.shopping.viewmodel.fakes.server.dto.toObject

class MockProductRepositoryImpl(
    private val client: OkHttpClient,
    private val baseUrl: String,
) : ProductRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): ApiResult<List<Product>> =
        withContext(Dispatchers.IO) {
            try {
                val offset = page * pageSize
                val url = "${baseUrl}products?offset=$offset&limit=$pageSize"

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext ApiResult.Error(response.code, response.message)

                    val responseBody = response.body.string() ?: ""
                    val webResponse = json.decodeFromString<List<MockProductResponse>>(responseBody)
                    ApiResult.Success(webResponse.map { it.toObject() })
                }
            } catch (e: Exception) {
                ApiResult.Exception(e)
            }
        }

    override suspend fun getProduct(id: Long): ApiResult<Product> =
        withContext(Dispatchers.IO) {
            try {
                val url = "${baseUrl}products/$id"

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext ApiResult.Error(response.code, response.message)

                    val responseBody = response.body.string() ?: ""
                    val webResponse = json.decodeFromString<MockProductResponse>(responseBody)
                    ApiResult.Success(webResponse.toObject())
                }
            } catch (e: Exception) {
                ApiResult.Exception(e)
            }
        }

    override suspend fun getCategoryProducts(
        page: Int,
        pageSize: Int,
        category: String,
    ): ApiResult<List<Product>> =
        withContext(Dispatchers.IO) {
            try {
                val offset = page * pageSize
                val url = "${baseUrl}products?offset=$offset&limit=$pageSize"

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext ApiResult.Error(response.code, response.message)

                    val responseBody = response.body.string() ?: ""
                    val webResponse = json.decodeFromString<List<MockProductResponse>>(responseBody)
                    ApiResult.Success(webResponse.map { it.toObject() }.filter { it.category == category })
                }
            } catch (e: Exception) {
                ApiResult.Exception(e)
            }
        }
}
