package woowacourse.shopping.mockwebserver

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class CouponMockWebserver {
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
                            path.startsWith("/coupons/discount") -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody(getCouponDiscount())
                            }
                            path == "/coupons" -> MockResponse()
                                .setResponseCode(200)
                                .setBody(getCoupons())

                            else -> MockResponse().setResponseCode(404)
                        }
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        private fun getCoupons(): String {
            return List(2) {
                """
                {
                    "id": ${it + 1},
                    "name": "할인쿠폰"
                }
    """
            }.joinToString(",", prefix = "[", postfix = "]").trimIndent()
        }

        private fun getCouponDiscount(): String {
            return """
                    {
                        "discountPrice": 5000,
                        "totalPrice": 10000
                    }
            """.trimIndent()
        }
    }
}
