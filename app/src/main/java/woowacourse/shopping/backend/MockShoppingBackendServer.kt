package woowacourse.shopping.backend

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.model.Product

class MockShoppingBackendServer {
    private val mockWebServer = MockWebServer()
    private val products: List<Product> = MockProductSeedData.products
    private val productsJson: String = products.toProductsJson()

    fun start(port: Int? = null): HttpUrl {
        mockWebServer.dispatcher = createDispatcher()
        if (port == null) {
            mockWebServer.start()
        } else {
            mockWebServer.start(port)
        }
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
                    "/products" -> jsonResponse(productsJson)
                    null -> MockResponse().setResponseCode(400)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

    private fun jsonResponse(body: String): MockResponse =
        MockResponse()
            .setHeader("Content-Type", "application/json")
            .setResponseCode(200)
            .setBody(body)

    private fun List<Product>.toProductsJson(): String =
        JSONArray()
            .apply { forEach { product -> put(product.toJson()) } }
            .toString()

    private fun Product.toJson(): JSONObject =
        JSONObject()
            .put("id", id)
            .put("name", getTitle())
            .put("price", getPrice())
            .put("imageUrl", imageUrl)
}
