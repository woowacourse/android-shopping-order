package woowacourse.shopping.datasource.product

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ProductMockServer {
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
                    path.startsWith("/products/cart-items") -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getProducts())
                    }
                    path.startsWith("/products") -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getProduct())
                    }
                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        private fun getProducts(): String {
            return """{
                      "products" : [ {
                        "product" : {
                          "id" : 2,
                          "name" : "샐러드",
                          "price" : 2000,
                          "imageUrl" : "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                        },
                        "cartItem" : {
                          "id" : 2,
                          "quantity" : 4
                        }
                      }, {
                        "product" : {
                          "id" : 1,
                          "name" : "치킨",
                          "price" : 10000,
                          "imageUrl" : "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                        },
                        "cartItem" : {
                          "id" : 1,
                          "quantity" : 10
                        }
                      } ],
                      "last" : true
                    }
            """.trimIndent()
        }

        private fun getProduct(): String {
            return """
                {
                        "product" : {
                          "id" : 1,
                          "name" : "치킨",
                          "price" : 10000,
                          "imageUrl" : "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                        },
                        "cartItem" : {
                          "id" : 1,
                          "quantity" : 10
                        }
                      }
            """.trimIndent()
        }

        private fun getParameters(queryString: String): Map<String, String> {
            val parameters = mutableMapOf<String, String>()

            val query = queryString.substringAfter("?")
            val parameterPairs = query.split("&")

            for (pair in parameterPairs) {
                val (key, value) = pair.split("=")
                parameters[key] = value
            }

            return parameters
        }
    }
}
