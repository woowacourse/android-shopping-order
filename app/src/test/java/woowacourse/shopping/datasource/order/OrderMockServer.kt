package woowacourse.shopping.datasource.order

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class OrderMockServer {
    private val mockWebServer = MockWebServer()
    val url = mockWebServer.url("").toString()

    init {
        mockWebServer.dispatcher = dispatcher
        mockWebServer.url("/")
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    companion object {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/orders" && request.method == "GET" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getOrders())
                    }
                    path.startsWith("/orders") && request.method == "GET" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getOrder())
                    }
                    path.startsWith("/orders") && request.method == "POST" -> { // PATCH
                        MockResponse()
                            .setResponseCode(201)
                            .setHeader("Location", "/orders/1")
                    }
                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        private fun getOrders(): String {
            return """
                    {
                      "orders" : [ {
                        "orderId" : 1,
                        "orderedDateTime" : "2023-06-05 07:21:47",
                        "products" : [ {
                          "product" : {
                            "id" : 1,
                            "name" : "치킨",
                            "price" : 10000,
                            "imageUrl" : "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                          },
                          "quantity" : 3
                        }, {
                          "product" : {
                            "id" : 3,
                            "name" : "피자",
                            "price" : 13000,
                            "imageUrl" : "https://images.unsplash.com/photo-1595854341625-f33ee10dbf94?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
                          },
                          "quantity" : 2
                        } ],
                        "totalPrice" : 56000
                      } ]
                    }
            """.trimIndent()
        }
        private fun getOrder(): String {
            return """
                {
                  "orderId" : 1,
                  "orderedDateTime" : "2023-06-05 07:21:47",
                  "products" : [ {
                    "product" : {
                      "id" : 1,
                      "name" : "치킨",
                      "price" : 10000,
                      "imageUrl" : "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                    },
                    "quantity" : 3
                  }, {
                    "product" : {
                      "id" : 3,
                      "name" : "피자",
                      "price" : 13000,
                      "imageUrl" : "https://images.unsplash.com/photo-1595854341625-f33ee10dbf94?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80"
                    },
                    "quantity" : 2
                  } ],
                  "totalPrice" : 56000
                }
            """.trimIndent()
        }
    }
}
