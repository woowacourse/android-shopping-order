@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import woowacourse.shopping.repository.http.coupon.CouponNetworkException
import woowacourse.shopping.repository.http.coupon.CouponParsingException
import woowacourse.shopping.repository.http.coupon.CouponResponseException
import woowacourse.shopping.repository.http.coupon.HttpCouponRepository
import java.time.LocalDate

class HttpCouponRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: HttpCouponRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        repository =
            HttpCouponRepository(
                client = OkHttpClient(),
                baseUrl = mockWebServer.url("/").toString(),
            )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `쿠폰 목록 API 성공 응답을 도메인 모델로 변환한다`() =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "coupons": [
                            {
                              "id": 1,
                              "code": "FIXED5000",
                              "title": "5,000원 할인 쿠폰",
                              "description": "100,000원 이상 주문 시 5,000원을 할인합니다.",
                              "expirationDate": "2026-12-31",
                              "minimumOrderAmount": 100000,
                              "fixedDiscountAmount": 5000
                            },
                            {
                              "id": 2,
                              "code": "FREESHIPPING",
                              "title": "무료 배송 쿠폰",
                              "description": "50,000원 이상 주문 시 배송비를 무료로 처리합니다.",
                              "expirationDate": "2026-10-31",
                              "minimumOrderAmount": 50000,
                              "freeShipping": true
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            )

            val actual = repository.getCoupons()
            val request = mockWebServer.takeRequest()

            assertEquals("/coupons", request.requestUrl?.encodedPath)
            assertEquals(2, actual.size)
            assertEquals("FIXED5000", actual.first().code)
            assertEquals(LocalDate.of(2026, 12, 31), actual.first().expirationDate)
            assertTrue(actual.last().freeShipping)
        }

    @Test
    fun `쿠폰 목록 API가 서버 오류를 반환하면 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"server error"}"""),
        )

        val actual =
            assertThrows<CouponResponseException> {
                runBlocking { repository.getCoupons() }
            }

        assertEquals(500, actual.code)
    }

    @Test
    fun `쿠폰 목록 API 네트워크 호출에 실패하면 예외를 던진다`() {
        val disconnectedServer = MockWebServer()
        disconnectedServer.start()
        val baseUrl = disconnectedServer.url("/").toString()
        disconnectedServer.shutdown()

        val disconnectedRepository =
            HttpCouponRepository(
                client = OkHttpClient(),
                baseUrl = baseUrl,
            )

        assertThrows<CouponNetworkException> {
            runBlocking { disconnectedRepository.getCoupons() }
        }
    }

    @Test
    fun `쿠폰 목록 API가 빈 응답 본문을 반환하면 파싱 예외를 던진다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(""),
        )

        val actual =
            assertThrows<CouponParsingException> {
                runBlocking { repository.getCoupons() }
            }

        assertTrue(actual.message?.contains("응답") == true)
    }
}
