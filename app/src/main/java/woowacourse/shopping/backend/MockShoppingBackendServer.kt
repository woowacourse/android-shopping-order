package woowacourse.shopping.backend

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import java.net.InetAddress
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle

class MockShoppingBackendServer {
    private val mockWebServer = MockWebServer()

    fun start(): HttpUrl {
        mockWebServer.dispatcher = createDispatcher()
        mockWebServer.start(InetAddress.getByName("127.0.0.1"), 0)
        return mockWebServer.url("/")
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    private fun createDispatcher(): Dispatcher =
        object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path?.substringBefore("?")
                return when (path) {
                    "/products" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(PRODUCTS_JSON)
                    }

                    null -> MockResponse().setResponseCode(400)
                    else -> {
                        when {
                            path.matches(Regex("^/products/\\d+$")) -> productResponse(path)
                            path == "/cart-items" -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                                    .setBody(CART_ITEMS_JSON)
                            }

                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                }
            }

            private fun productResponse(path: String): MockResponse {
                val productId = path.substringAfterLast("/").toLongOrNull()
                val product = products.find { currentProduct -> currentProduct.id == productId }
                if (product == null) {
                    return MockResponse().setResponseCode(404)
                }
                return MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setResponseCode(200)
                    .setBody(product.toJsonString())
            }
        }

    companion object {
        private val products: List<Product> =
            List(12) { index ->
                listOf(
                    Product(
                        id = (1 + index * 4).toLong(),
                        title = ProductTitle("동원 스위트콘"),
                        price = Price(99_800),
                        imageUrl = "https://img.dongwonmall.com/dwmall/static_root/model_img/main/153/15327_1_a.jpg?f=webp&q=80",
                    ),
                    Product(
                        id = (2 + index * 4).toLong(),
                        title = ProductTitle("딸기 바나나 주스"),
                        price = Price(29_800),
                        imageUrl = "https://top-brix.com/data/file/b212/1994115392_UoXNtigA_92fde4ac2d6e1986b0f5a3ed9fa18ad5b2257278.jpg",
                    ),
                    Product(
                        id = (3 + index * 4).toLong(),
                        title = ProductTitle("아이스 아메리카노"),
                        price = Price(9_800),
                        imageUrl = "https://t1.daumcdn.net/cafeattach/1Frx7/6e867e71391691ae3dd805cbcf58c5c32c898dd6",
                    ),
                    Product(
                        id = (4 + index * 4).toLong(),
                        title = ProductTitle("초코 바나나 스무디"),
                        price = Price(49_800),
                        imageUrl = "https://reciup.com/assets/recipe/202402/e05f83ab-54be-40e9-aca2-27ba50791078.png",
                    ),
                )
            }.flatten()

        private val PRODUCTS_JSON =
            JSONArray().apply {
                products.forEach { product ->
                    put(product.toJsonObject())
                }
            }.toString()

        private val CART_ITEMS_JSON =
            JSONArray().apply {
                val firstProduct = products.first()
                val secondProduct = products[1]
                put(
                    JSONObject()
                        .put("id", 1L)
                        .put("quantity", 5)
                        .put("product", firstProduct.toJsonObject()),
                )
                put(
                    JSONObject()
                        .put("id", 2L)
                        .put("quantity", 1)
                        .put("product", secondProduct.toJsonObject()),
                )
            }.toString()

        private fun Product.toJsonString(): String = toJsonObject().toString()

        private fun Product.toJsonObject(): JSONObject =
            JSONObject()
                .put("id", id)
                .put("title", getTitle())
                .put("price", getPrice())
                .put("imageUrl", imageUrl)
    }
}
