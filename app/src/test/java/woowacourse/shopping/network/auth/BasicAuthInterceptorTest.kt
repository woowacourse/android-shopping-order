package woowacourse.shopping.network.auth

import junit.framework.TestCase.assertEquals
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BasicAuthInterceptorTest {
    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `이미 Authorization 헤더가 있으면 기존 값을 유지한다`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(
                    BasicAuthInterceptor {
                        BasicAuthHeaderFactory.create()
                    },
                ).build()

        client
            .newCall(
                Request
                    .Builder()
                    .url(mockWebServer.url("/products"))
                    .header("Authorization", "Bearer existing-token")
                    .build(),
            ).execute()
            .close()

        val request = mockWebServer.takeRequest()
        assertEquals("Bearer existing-token", request.getHeader("Authorization"))
    }
}
