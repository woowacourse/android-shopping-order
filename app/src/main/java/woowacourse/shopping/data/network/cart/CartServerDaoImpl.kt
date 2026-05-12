package woowacourse.shopping.data.network.cart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.domain.CartContent

class CartServerDaoImpl(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl,
    private val json: Json,
) : CartServerDao {
    override suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent> = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("cart-items")
            .addQueryParameter("page", startIndex.toString())
            .addQueryParameter("size", pageSize.toString())
            .build()

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
            json.decodeFromString<CartItemDto>(body)
                .content.map(Content::toDomain)
        }
    }

    override suspend fun getTotalQuantity(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(item: CartContent) {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: CartContent) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: String) {
        TODO("Not yet implemented")
    }
}
