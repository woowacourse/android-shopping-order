package woowacourse.shopping.datasource.cart

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CartMockServer {
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
                    path.startsWith("/cart-items") && request.method == "GET" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getCartProducts())
                    }
                    path.startsWith("/cart-items") && request.method == "POST" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setHeader("Location", "/cart-items/1")
                            .setResponseCode(200)
                    }
                    path.startsWith("/cart-items") && request.method == "PATCH" -> { // PATCH
                        MockResponse()
                            .setResponseCode(200)
                    }
                    path.startsWith("/cart-items") && request.method == "DELETE" -> { // PATCH
                        MockResponse()
                            .setResponseCode(204)
                    }
                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        private fun getCartProducts(): String {
            return """
                [ {
                  "id" : 1,
                  "quantity" : 10,
                  "product" : {
                    "id" : 1,
                    "name" : "치킨",
                    "price" : 10000,
                    "imageUrl" : "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                  }
                }, {
                  "id" : 2,
                  "quantity" : 4,
                  "product" : {
                    "id" : 2,
                    "name" : "샐러드",
                    "price" : 2000,
                    "imageUrl" : "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"
                  }
                } ]
            """.trimIndent()
        }
    }
}
