//package woowacourse.shopping.data.service.product
//
//import okhttp3.RequestBody.Companion.toRequestBody
//import woowacourse.shopping.ShoppingApplication.Companion.pref
//import woowacourse.shopping.data.model.Product
//import woowacourse.shopping.data.service.cart.ProductId
//import woowacourse.shopping.data.util.convertJsonToProduct
//import woowacourse.shopping.data.util.convertJsonToProducts
//import woowacourse.shopping.data.util.okhttp.Header.Companion.JSON_MEDIA_TYPE
//import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient
//import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.delete
//import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.get
//import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.post
//import woowacourse.shopping.data.util.okhttp.ShoppingOkHttpClient.put
//import woowacourse.shopping.data.util.toJson
//import woowacourse.shopping.server.ShoppingMockWebServer
//
//class ProductServiceImpl : ProductService {
//    private val shoppingMockServer: ShoppingMockWebServer = ShoppingMockWebServer()
//    private val baseUrl: String = pref.getBaseUrl().toString()
//
//    init {
//        shoppingMockServer.start()
//        shoppingMockServer.join()
//    }
//
//    override fun findProductById(id: ProductId): Product? {
//        val url = "$baseUrl/products/$id"
//        var product: Product? = null
//
//        val latch = ShoppingOkHttpClient.enqueue(
//            get(url),
//            onSuccess = { _, response ->
//                response.body?.string()?.let { product = it.convertJsonToProduct() }
//            },
//            onFailed = { _, _ -> },
//        )
//        latch.await()
//        return product
//    }
//
//    override fun getAllProduct(): List<Product> {
//        val url = "$baseUrl/products"
//        var products = mutableListOf<Product>()
//
//        val latch = ShoppingOkHttpClient.enqueue(
//            get(url),
//            onSuccess = { _, response ->
//                response.body?.string()?.let { products = it.convertJsonToProducts() }
//            },
//            onFailed = { _, _ -> },
//        )
//        latch.await()
//        return products.toList()
//    }
//
//    override fun insertProduct(product: Product) {
//        val url = "$baseUrl/products"
//
//        val latch = ShoppingOkHttpClient.enqueue(
//            post(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
//            onSuccess = { _, _ -> },
//            onFailed = { _, _ -> },
//        )
//        latch.await()
//    }
//
//    override fun updateProduct(product: Product) {
//        val url = "$baseUrl/products/${product.id}"
//
//        val latch = ShoppingOkHttpClient.enqueue(
//            put(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
//            onSuccess = { _, _ -> },
//            onFailed = { _, _ -> },
//        )
//        latch.await()
//    }
//
//    override fun deleteProduct(product: Product) {
//        val url = "$baseUrl/products/${product.id}"
//
//        val latch = ShoppingOkHttpClient.enqueue(
//            delete(url, product.toJson().toRequestBody(JSON_MEDIA_TYPE)),
//            onSuccess = { _, _ -> },
//            onFailed = { _, _ -> },
//        )
//        latch.await()
//    }
//
//    companion object {
//        const val JSON_NAME = "name"
//        const val JSON_PRICE = "price"
//        const val JSON_IMAGE_URL = "imageUrl"
//    }
//}
