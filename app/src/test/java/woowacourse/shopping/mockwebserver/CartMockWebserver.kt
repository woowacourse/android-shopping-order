package woowacourse.shopping.mockwebserver

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CartMockWebserver {
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
                        when (path) {
                            "/cart-items" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody(getCarts())
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "POST" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when (path) {
                            "/cart-items" -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                                    .setBody(getCart())
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "PATCH" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when {
                            path.startsWith("/cart-items") -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "DELETE" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when {
                            path.startsWith("/cart-items") -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        private fun getCarts(): String {
            return List(10) {
                """
                {
                    "id": $it,
                    "quantity": ${it + 1},
                    "product": {
                        "id": $it,
                        "name": "상품 $it",
                        "price": ${it * 1000},
                        "imageUrl": ""
                        }
                }
                """
            }.joinToString(",", prefix = "[", postfix = "]").trimIndent()
        }

        private fun getCart(): String {
            return """
                {
                    "id": 1,
                    "quantity": 1,
                    "product": {
                        "id": 1,
                        "name": "상품 1",
                        "price": 1000,
                        "imageUrl": ""
                        }
                }
            """.trimIndent()
        }
    }
}
