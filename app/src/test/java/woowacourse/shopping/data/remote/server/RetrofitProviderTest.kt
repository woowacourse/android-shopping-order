package woowacourse.shopping.data.remote.server

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.remote.server.service.CouponService

class RetrofitProviderTest {
    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `BASE_URL 끝에 슬래시가 없어도 요청 경로를 정상적으로 만든다`(): Unit =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse
                    .Builder()
                    .code(200)
                    .body("[]")
                    .addHeader("Content-Type", "application/json")
                    .build(),
            )
            val baseUrlWithoutTrailingSlash = mockWebServer.url("/").toString().removeSuffix("/")
            val couponService =
                RetrofitProvider(
                    authHeaderProvider = { null },
                    baseUrl = baseUrlWithoutTrailingSlash,
                ).create(CouponService::class.java)

            couponService.requestCoupons()

            val request = mockWebServer.takeRequest()
            request.target shouldBe "/coupons"
        }
}
