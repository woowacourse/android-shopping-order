package woowacourse.shopping.data.network.product

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.data.network.product.dto.ProductDto
import woowacourse.shopping.data.network.product.dto.ProductPageResponse
import woowacourse.shopping.domain.Product

class ProductServerDaoImpl(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl,
    private val json: Json,
) : ProductDao {
    override suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product> = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("products")
            .addQueryParameter("page", startIndex.toString())
            .addQueryParameter("size", pageSize.toString())
            .build()

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
            json.decodeFromString<ProductPageResponse>(body)
                .content.map(ProductDto::toDomain)
        }
    }

    override suspend fun findById(id: String): Product = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("products")
            .addPathSegment(id)
            .build()

        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
            json.decodeFromString<ProductDto>(body).toDomain()
        }
    }
}
