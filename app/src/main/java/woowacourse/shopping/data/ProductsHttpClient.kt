package woowacourse.shopping.data

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.data.product.dto.ProductResponse

class ProductsHttpClient(
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
) {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            ).build()

    fun getProductById(id: Long): ProductResponse {
        val response = httpGet("/products/$id")
        val jsonString = response.body?.string() ?: ""
        return Json.decodeFromString(jsonString)
    }

    fun getShoppingCart(
        page: Int,
        size: Int,
    ): Response = httpGet("/cart-items?page=$page&size=$size&sort=string")

    fun postShoppingCartItem(
        id: Long,
        quantity: Int,
    ): Response = run("/cart-items")

    fun getProducts(
        page: Int,
        size: Int,
    ): Response = httpGet("/products?page=$page&size=$size")

    fun getCartItems(): Response = run("/cart-items")

    private fun run(path: String): Response {
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .build()

        return client.newCall(request).execute()
    }

    private fun httpGet(path: String): Response {
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .get()
                .build()

        return client.newCall(request).execute()
    }
}
