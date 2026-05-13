package woowacourse.shopping.data.network.cart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

    override suspend fun getTotalQuantity(): Int? = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("cart-items")
            .addPathSegment("counts")
            .build()

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
            json.decodeFromString<Quantity>(body)
                .quantity
        }
    }

    override suspend fun insert(item: CartContent) = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("cart-items")
            .build()
        val jsonBody = CartItemInsertDto(
            productId = item.productId.toLong(),
            quantity = item.quantity,
        )
        val body = json.encodeToString(jsonBody)
        val request = Request
            .Builder()
            .url(url)
            .post(
                body.toRequestBody(),
            )
            .build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
        }
    }

    override suspend fun update(item: CartContent) = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("cart-items")
            .addPathSegment(item.productId)
            .build()

        val jsonBody = Quantity(item.quantity)

        val body = json.encodeToString(jsonBody)

        val request = Request.Builder().url(url).patch(body.toRequestBody()).build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
        }
    }

    override suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        val url = baseUrl.newBuilder()
            .addPathSegment("cart-items")
            .addPathSegment(id)
            .build()

        val request = Request.Builder().url(url).delete().build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "products 요청 실패: ${response.code}" }

            val body = response.body?.string().orEmpty()
        }
    }
}
