package woowacourse.shopping.utils.mockWebServer

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CartMockWeb {
    private val mockWebServer = MockWebServer()

    val url = mockWebServer.url("").toString()

    init {
        mockWebServer.dispatcher = dispatcher
        mockWebServer.url("/")
    }

    fun takeRequest(): RecordedRequest {
        return mockWebServer.takeRequest()
    }

    companion object {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.method) {
                    "POST" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when {
                            path.startsWith("/cart-items") -> {
                                val cartItemId = path.substringAfterLast("/").toInt()
                                val locationHeader = "/cart-items/$cartItemId"
                                MockResponse()
                                    .setResponseCode(201)
                                    .addHeader("Location", locationHeader)
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "GET" -> {
                        when (request.path) {
                            "/cart-items" -> MockResponse()
                                .setResponseCode(200)
                                .setBody(getCartProducts())

                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "PATCH" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when {
                            path.startsWith("/cart-items") -> {
                                MockResponse()
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
                                    .setResponseCode(200)
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        private fun getCartProducts(): String {
            return List(7) {
                """
                {
                    "id": $it,
                    "quantity": ${it + 1},
                    "product": {
                        "name": "치킨$it",
                        "price": 10000,
                        "imageUrl":  "https://search4.kakaocdn.net/argon/656x0_80_wr/KjhZ1Chrw9p"
                    }
                }
    """
            }.joinToString(",", prefix = "[", postfix = "]").trimIndent()
        }
    }
}
