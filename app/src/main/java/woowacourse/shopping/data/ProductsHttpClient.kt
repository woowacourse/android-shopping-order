package woowacourse.shopping.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ProductsHttpClient(
    private val baseUrl: String = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com",
) {
    private val client: OkHttpClient = OkHttpClient()

    fun getShoppingCart(
        page: Int,
        size: Int,
    ): Response {
        val request: Request =
            Request
                .Builder()
                .url("$baseUrl/cart-items?page=$page&size=$size&sort=string")
                .get()
                .build()

        return client.newCall(request).execute()
    }

    fun postShoppingCartItem(
        id: Long,
        quantity: Int,
    ): Response = run("/cart-items")

    fun getProducts(
        page: Int,
        size: Int,
    ): Response {
        val request: Request =
            Request
                .Builder()
                .url("$baseUrl/products?page=${page}&size=${size}")
                .get()
                .build()

        return client.newCall(request).execute()
    }

    fun getProductById(id: Long): Response = run("/products/$id")

    fun getCartItems(): Response = run("/cart-items")

    private fun run(path: String): Response {
        val request: Request =
            Request
                .Builder()
                .url(baseUrl + path)
                .build()

        return client.newCall(request).execute()
    }
}
