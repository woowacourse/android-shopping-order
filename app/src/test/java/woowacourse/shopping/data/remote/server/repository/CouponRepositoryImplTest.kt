package woowacourse.shopping.data.remote.server.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import woowacourse.shopping.data.remote.server.service.CouponService
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        val couponService = retrofit.create(CouponService::class.java)
        couponRepository = CouponRepositoryImpl(couponService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `쿠폰 목록 조회 응답을 쿠폰 도메인 모델로 변환한다`(): Unit =
        runBlocking {
            mockWebServer.enqueue(
                MockResponse
                    .Builder()
                    .code(200)
                    .body(couponResponseBody())
                    .addHeader("Content-Type", "application/json")
                    .build(),
            )

            val coupons = couponRepository.getCoupons()

            coupons[0].code shouldBe "FIXED5000"
            coupons[0].name shouldBe "5,000원 할인 쿠폰"
            val request = mockWebServer.takeRequest()
            request.target shouldBe "/coupons"
            request.method shouldBe "GET"
        }

    private fun couponResponseBody(): String =
        """
        [
          {
            "id": 1,
            "code": "FIXED5000",
            "description": "5,000원 할인 쿠폰",
            "expirationDate": "2026-11-30",
            "discountType": "fixed",
            "discount": 5000,
            "minimumAmount": 100000
          },
          {
            "id": 2,
            "code": "BOGO",
            "description": "2개 구매 시 1개 무료 쿠폰",
            "expirationDate": "2026-05-30",
            "discountType": "buyXgetY",
            "buyQuantity": 2,
            "getQuantity": 1
          },
          {
            "id": 3,
            "code": "FREESHIPPING",
            "description": "5만원 이상 구매 시 무료 배송 쿠폰",
            "expirationDate": "2026-08-31",
            "discountType": "freeShipping",
            "minimumAmount": 50000
          },
          {
            "id": 4,
            "code": "MIRACLESALE",
            "description": "미라클모닝 30% 할인 쿠폰",
            "expirationDate": "2026-07-31",
            "discountType": "percentage",
            "discount": 30,
            "availableTime": {
              "start": "04:00:00",
              "end": "07:00:00"
            }
          }
        ]
        """.trimIndent()
}
