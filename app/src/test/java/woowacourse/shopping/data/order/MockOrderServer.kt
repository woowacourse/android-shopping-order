package woowacourse.shopping.data.order

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object MockOrderServer {
    val server = MockWebServer()
    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: return MockResponse().setResponseCode(404)
            return when {
                path == "/pay" && request.method == "POST" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getPostOrderResponse())
                }
                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }
    }

    private fun getPostOrderResponse(): String {
        return """
            {
                "orderId": 1
            }
        """.trimIndent()
    }

    init {
        server.dispatcher = dispatcher
        server.start()
    }
}