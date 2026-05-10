package woowacourse.shopping.backend

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingItem
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpProductBackendDataSource(
    private val client: OkHttpClient,
    private val baseUrl: HttpUrl,
) : ProductBackendDataSource {
    override suspend fun fetchProducts(): List<ShoppingItem> {
        val request =
            Request
                .Builder()
                .url(baseUrl.newBuilder().addPathSegment("products").build())
                .get()
                .build()

        val response = client.newCall(request).await()
        response.use { currentResponse ->
            if (!currentResponse.isSuccessful) {
                throw IOException("상품 조회 실패: HTTP ${currentResponse.code}")
            }
            val body = currentResponse.body.string()
            return parseProducts(body)
        }
    }

    private fun parseProducts(productsJson: String): List<ShoppingItem> {
        val productsArray = JSONArray(productsJson)
        return List(productsArray.length()) { index ->
            val productJson = productsArray.getJSONObject(index)
            ShoppingItem(
                product =
                    Product(
                        id = productJson.getLong("id"),
                        title = ProductTitle(productJson.optString("title")),
                        price = Price(productJson.getInt("price")),
                        imageUrl = productJson.getString("imageUrl"),
                    ),
                quantity = 0,
            )
        }
    }

    private suspend fun Call.await(): Response =
        suspendCancellableCoroutine { continuation ->
            enqueue(
                object : Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException,
                    ) {
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(
                        call: Call,
                        response: Response,
                    ) {
                        continuation.resume(response)
                    }
                },
            )

            continuation.invokeOnCancellation { cancel() }
        }
}
