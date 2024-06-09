package woowacourse.shopping.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.coupon.AvailableTime
import woowacourse.shopping.data.model.coupon.CouponResponseItem
import woowacourse.shopping.utils.COUPONS_RESPONSE
import woowacourse.shopping.utils.getRetrofit

class CouponServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var couponService: CouponService
    private lateinit var mockResponse: MockResponse

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        couponService = getRetrofit(mockWebServer).create(CouponService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `쿠폰들을 불러올 수 있다`() =
        runTest {
            // given
            mockResponse =
                MockResponse()
                    .setBody(COUPONS_RESPONSE.trimIndent())
                    .addHeader("Content-Type", "application/json")
            mockWebServer.enqueue(mockResponse)

            // when
            val response = couponService.getCoupons()

            // then
            assertThat(response).isEqualTo(
                listOf(
                    CouponResponseItem(
                        id = 1,
                        code = "FIXED5000",
                        description = "5,000원 할인 쿠폰",
                        expirationDate = "2024-11-30",
                        discount = 5000,
                        minimumAmount = 100000,
                        discountType = "fixed",
                        availableTime = null,
                        buyQuantity = null,
                        getQuantity = null,
                    ),
                    CouponResponseItem(
                        id = 2,
                        code = "BOGO",
                        description = "2개 구매 시 1개 무료 쿠폰",
                        expirationDate = "2024-05-30",
                        discount = null,
                        minimumAmount = null,
                        discountType = "buyXgetY",
                        availableTime = null,
                        buyQuantity = 2,
                        getQuantity = 1,
                    ),
                    CouponResponseItem(
                        id = 3,
                        code = "FREESHIPPING",
                        description = "5만원 이상 구매 시 무료 배송 쿠폰",
                        expirationDate = "2024-08-31",
                        discount = null,
                        minimumAmount = 50000,
                        discountType = "freeShipping",
                        availableTime = null,
                        buyQuantity = null,
                        getQuantity = null,
                    ),
                    CouponResponseItem(
                        id = 4,
                        code = "MIRACLESALE",
                        description = "미라클모닝 30% 할인 쿠폰",
                        expirationDate = "2024-07-31",
                        discount = 30,
                        minimumAmount = null,
                        discountType = "percentage",
                        availableTime =
                            AvailableTime(
                                start = "04:00:00",
                                end = "07:00:00",
                            ),
                        buyQuantity = null,
                        getQuantity = null,
                    ),
                ),
            )
        }
}
