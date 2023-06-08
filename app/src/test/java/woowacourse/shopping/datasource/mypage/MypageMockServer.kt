package woowacourse.shopping.datasource.mypage

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class MypageMockServer {
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
                    path == "/members/cash" && request.method == "POST" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getTotalCash())
                    }
                    path == "/members/cash" && request.method == "GET" -> {
                        MockResponse()
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200)
                            .setBody(getTotalCash())
                    }
                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        private fun getTotalCash() = """
            {
              "totalCash" : 15000
            }
        """.trimIndent()
    }
}
