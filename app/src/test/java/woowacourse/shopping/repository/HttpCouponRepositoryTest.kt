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
import woowacourse.shopping.data.remote.coupon.CouponNetworkException
import woowacourse.shopping.data.remote.coupon.CouponParsingException
import woowacourse.shopping.data.remote.coupon.CouponResponseException
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.domain.model.coupon.FreeShippingPolicy
import woowacourse.shopping.domain.model.coupon.OrderFixedAmountDiscountPolicy
import woowacourse.shopping.domain.model.coupon.OrderPercentageDiscountPolicy
import woowacourse.shopping.domain.model.coupon.SameProductQuantityDiscountPolicy
import java.time.LocalDate

class CouponRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: CouponRepositoryImpl

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        repository =
            CouponRepositoryImpl(
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
                        [
                          {
                            "id": 1,
                            "code": "FIXED5000",
                            "description": "5,000원 할인 쿠폰",
                            "expirationDate": "2026-12-31",
                            "minimumAmount": 100000,
                            "discount": 5000,
                            "discountType": "fixed"
                          },
                          {
                            "id": 2,
                            "code": "FREESHIP50000",
                            "description": "50,000원 이상 구매 시 무료 배송",
                            "expirationDate": "2026-10-31",
                            "minimumAmount": 50000,
                            "discountType": "freeShipping"
                          },
                          {
                            "id": 3,
                            "code": "LUNCH15",
                            "description": "점심시간 15% 할인 쿠폰",
                            "expirationDate": "2026-12-31",
                            "discount": 15,
                            "availableTime": {
                              "start": "11:00:00",
                              "end": "14:00:00"
                            },
                            "discountType": "percentage"
                          },
                          {
                            "id": 4,
                            "code": "BUY2GET1",
                            "description": "2+1 쿠폰",
                            "expirationDate": "2026-12-31",
                            "buyQuantity": 2,
                            "getQuantity": 1,
                            "discountType": "buyXgetY"
                          }
                        ]
                        """.trimIndent(),
                    ),
            )

            val actual = repository.getCoupons()
            val request = mockWebServer.takeRequest()

            assertEquals("/coupons", request.requestUrl?.encodedPath)
            assertEquals(4, actual.size)
            assertEquals("FIXED5000", actual.first().code)
            assertEquals(LocalDate.of(2026, 12, 31), actual.first().expirationDate)
            assertEquals(OrderFixedAmountDiscountPolicy(amount = 5_000), actual[0].policy)
            assertTrue(actual[1].policy === FreeShippingPolicy)
            assertEquals(11, actual[2].availableFromHour)
            assertEquals(14, actual[2].availableToHourExclusive)
            assertEquals(OrderPercentageDiscountPolicy(rate = 15), actual[2].policy)
            assertEquals(SameProductQuantityDiscountPolicy(requiredSameProductQuantity = 3), actual[3].policy)
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
            CouponRepositoryImpl(
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
