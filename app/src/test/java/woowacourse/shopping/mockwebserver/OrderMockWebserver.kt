package woowacourse.shopping.mockwebserver

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class OrderMockWebserver {
    private val mockWebServer: MockWebServer = MockWebServer()

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
                            path == "/orders" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody(getOrders())
                            }
                            path.startsWith("/orders") -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                                    .setBody(getOrder())
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    "POST" -> {
                        val path = request.path ?: return MockResponse().setResponseCode(404)
                        when (path) {
                            "/orders" -> {
                                MockResponse()
                                    .setHeader("Content-Type", "application/json")
                                    .setResponseCode(200)
                                    .setBody(getOrder())
                            }
                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        private fun getOrder(): String {
            return """
                {
                    "id": 1,
                    "orderProducts": [
                      {
                        "productResponse": {
                          "id": 1,
                          "name": "치킨",
                          "price": 10000,
                          "imageUrl": "http://test.jpg"
                        },
                        "quantity": 1
                      }
                    ],
                    "timestamp": "2023-05-30T17:00:00",
                    "couponName": "할인쿠폰",
                    "originPrice": 10000,
                    "discountPrice": 5000,
                    "totalPrice": 5000
                  }
            """.trimIndent()
        }

        private fun getOrders(): String {
            return List(10) {
                """
                    {
                    "id": 1,
                    "orderProducts": [
                      {
                        "productResponse": {
                          "id": 1,
                          "name": "치킨",
                          "price": 10000,
                          "imageUrl": "http://test.jpg"
                        },
                        "quantity": 1
                      }
                    ],
                    "timestamp": "2023-05-30T17:00:00",
                    "couponName": "할인쿠폰",
                    "originPrice": 10000,
                    "discountPrice": 5000,
                    "totalPrice": 5000
                  }
                """
            }.joinToString(",", prefix = "[", postfix = "]").trimIndent()
        }
    }
}
