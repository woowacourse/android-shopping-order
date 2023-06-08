package woowacourse.shopping.data.product

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object MockProductServer {
    val server = MockWebServer()
    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: return MockResponse().setResponseCode(404)
            return when {
                path == "/products" && request.method == "GET" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getProductsResponse())
                }
                path == "/products/1" && request.method == "GET" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getProductResponse())
                }
                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }
    }

    private fun getProductsResponse(): String {
        return """
            [
                {
                    "id": 1,
                    "name": "치킨",
                    "price": 10000,
                    "imageUrl": "http://example.com/chicken.jpg"
                },
                {
                    "id": 2,
                    "name": "피자",
                    "price": 20000,
                    "imageUrl": "http://example.com/pizza.jpg"
                }
            ]
        """.trimIndent()
    }

    private fun getProductResponse(): String {
        return """
            {
                "id": 1,
                "name": "치킨",
                "price": 10000,
                "imageUrl": "http://example.com/chicken.jpg"
            }
        """.trimIndent()
    }

    init {
        server.dispatcher = dispatcher
        server.start()
    }
}