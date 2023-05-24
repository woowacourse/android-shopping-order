package woowacourse.shopping.data.service

import okhttp3.RequestBody.Companion.toRequestBody
import woowacourse.shopping.data.model.Page
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.util.convertJsonToProduct
import woowacourse.shopping.data.util.convertJsonToProducts
import woowacourse.shopping.data.util.okhttp.Header.Companion.JSON_MEDIA_TYPE
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.delete
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.get
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.post
import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.put
import woowacourse.shopping.data.util.toJson
import woowacourse.shopping.server.ShoppingMockWebServer

class ProductServiceImpl : ProductService {
    private val shoppingMockServer: ShoppingMockWebServer = ShoppingMockWebServer()
    private var BASE_URL: String = "http://3.36.62.54:8080"

    init {
        shoppingMockServer.start()
        shoppingMockServer.join()
    }

    override fun getProductByPage(page: Page): List<Product> {
        val url = "$BASE_URL/products?start=${page.start}&count=${page.sizePerPage}"
        var products = mutableListOf<Product>()

        val latch = ShoppingOkHttpClient.enqueue(
            get(url),
            onSuccess = { _, response ->
                response.body?.string()?.let { products = it.convertJsonToProducts() }
            },
            onFailed = { _, _ -> },
        )
        latch.await()
        return products.toList()
    }

    override fun getAllProduct(): List<Product> {
        val url = "$BASE_URL/products"
        var products = mutableListOf<Product>()

        val latch = ShoppingOkHttpClient.enqueue(
            get(url),
            onSuccess = { _, response ->
                response.body?.string()?.let { products = it.convertJsonToProducts() }
            },
            onFailed = { _, _ -> },
        )
        latch.await()
        return products.toList()
    }

    override fun findProductById(id: Int): Product? {
        val url = "$BASE_URL/products/$id"
        var product: Product? = null

        val latch = ShoppingOkHttpClient.enqueue(
            get(url),
            onSuccess = { _, response ->
                response.body?.string()?.let { product = it.convertJsonToProduct() }
            },
            onFailed = { _, _ -> },
        )
        latch.await()
        return product
    }

    override fun addProduct(product: Product) {
        val url = "$BASE_URL/products"

        val latch = ShoppingOkHttpClient.enqueue(
            post(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun adjustProduct(product: Product) {
        val url = "$BASE_URL/products/${product.id}"

        val latch = ShoppingOkHttpClient.enqueue(
            put(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    override fun deleteProduct(product: Product) {
        val url = "$BASE_URL/products/${product.id}"

        val latch = ShoppingOkHttpClient.enqueue(
            delete(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
            onSuccess = { _, _ -> },
            onFailed = { _, _ -> },
        )
        latch.await()
    }

    companion object {
        const val JSON_NAME = "name"
        const val JSON_PRICE = "price"
        const val JSON_IMAGE_URL = "imageUrl"
    }
}
