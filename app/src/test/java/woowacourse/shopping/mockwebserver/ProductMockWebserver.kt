package woowacourse.shopping.mockwebserver

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ProductMockWebserver {
    private val mockWebServer = MockWebServer()

    val url = mockWebServer.url("").toString()

    init {
        mockWebServer.dispatcher = dispatcher
        mockWebServer.url("/")
    }

    companion object {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.method) {
                    "GET" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when {
                            path == "/products" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody(getProducts())
                            }
                            path.startsWith("/products") -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                                    .setBody(getProduct())
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        private fun getProducts(): String {
            return List(1) {
                """
                {
                    "id": ${it + 1},
                    "name": "상품${it + 1}",
                    "price": 10000,
                    "imageUrl": ""
                }
                """
            }.joinToString(",", prefix = "[", postfix = "]").trimIndent()
        }

        private fun getProduct(): String {
            return """
                {
                    "id": 1,
                    "name": "상품",
                    "price": 10000,
                    "imageUrl": ""
                }
            """.trimIndent()
        }
    }
}
