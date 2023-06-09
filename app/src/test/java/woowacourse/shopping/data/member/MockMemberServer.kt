package woowacourse.shopping.data.member

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object MockMemberServer {
    val server = MockWebServer()
    private val dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: return MockResponse().setResponseCode(404)
            return when {
                path == "/members/points" && request.method == "GET" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getPointsResponse())
                }
                path == "/members/orders" && request.method == "GET" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getOrderHistoryResponse())
                }
                path == "/members/orders/10" && request.method == "GET" -> {
                    MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(200)
                        .setBody(getOrderResponse())
                }
                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }
    }

    private fun getPointsResponse(): String {
        return """
            {
                "points": 5000
            }
        """.trimIndent()
    }

    private fun getOrderHistoryResponse(): String {
        return """
            [
                {
                  "orderId": 1,
                  "orderPrice": 25000,
                  "totalAmount": 2,
                  "previewName": "PET보틀-정사각(370ml)"
                },
                {
                  "orderId": 2,
                  "orderPrice": 1400,
                  "totalAmount": 3,
                  "previewName": "[든든] 동원 스위트콘"
                }
            ]
        """.trimIndent()
    }

    private fun getOrderResponse(): String {
        return """
            {
                "orderItems": [
                  {
                    "name": "[든든] 동원 스위트콘",
                    "imageUrl": "http://image/test1.png",
                    "quantity": 2,
                    "price": 99800
                  },
                  {
                    "name": "PET보틀-원형(500ml)",
                    "imageUrl": "http://image/test2.png",
                    "quantity": 3,
                    "price": 84400
                  }
                ],
                "originalPrice": 184400,
                "usedPoints": 1000,
                "orderPrice": 183400
            }
        """.trimIndent()
    }

    init {
        server.dispatcher = dispatcher
        server.start()
    }
}