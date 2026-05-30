package woowacourse.shopping.data.repository

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.remote.RetrofitClient
import woowacourse.shopping.data.source.remote.datasource.CouponRemoteDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.fake.dispatcher.FakeCouponDispatcher

class DefaultCouponRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: DefaultCouponRepository

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.dispatcher = FakeCouponDispatcher()
        server.start()

        val authDataSource =
            object : AuthDataSource {
                override suspend fun getToken(): String = ""

                override suspend fun saveToken(
                    id: String,
                    password: String,
                ): Unit = throw UnsupportedOperationException()
            }

        val retrofitClient =
            RetrofitClient(
                authDataSource = authDataSource,
                baseUrl = server.url("/").toString(),
            )

        repository =
            DefaultCouponRepository(
                remoteDataSource = CouponRemoteDataSource(retrofitClient.retrofit),
            )
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }

    @Test
    fun `쿠폰 목록을 도메인 모델로 변환해 반환한다`() =
        runTest {
            val coupons = repository.getCoupons()

            assertThat(coupons).hasSize(4)
        }

    @Test
    fun `Fixed 쿠폰의 id, discount, miminumAmount가 정상적으로 매핑된다`() =
        runTest {
            val coupons = repository.getCoupons()

            val fixed = coupons.filterIsInstance<Coupon.Fixed>().single()
            assertThat(fixed.id).isEqualTo(1L)
            assertThat(fixed.discount).isEqualTo(5000L)
            assertThat(fixed.minimumAmount).isEqualTo(Money(amount = 100_000L))
        }

    @Test
    fun `BuyXGetY 쿠폰의 id, buyQuantity, getQuantity가 정상저으로 매핑된다`() =
        runTest {
            val coupons = repository.getCoupons()

            val buyXGetY = coupons.filterIsInstance<Coupon.BuyXGetY>().single()

            assertThat(buyXGetY.id).isEqualTo(2L)
            assertThat(buyXGetY.buyQuantity).isEqualTo(2)
            assertThat(buyXGetY.getQuantity).isEqualTo(1)
        }

    @Test
    fun `FreeShipping 쿠폰의 id, mimunumAmount가 정상적으로 매핑된다`() =
        runTest {
            val coupons = repository.getCoupons()

            val freeShipping = coupons.filterIsInstance<Coupon.FreeShipping>().single()

            assertThat(freeShipping.id).isEqualTo(3L)
            assertThat(freeShipping.minimumAmount).isEqualTo(Money(amount = 50_000L))
        }

    @Test
    fun `Percentage 쿠폰의 id, discount, availableTime이 정상적으로 매핑된다`() =
        runTest {
            val coupons = repository.getCoupons()

            val percentage = coupons.filterIsInstance<Coupon.Percentage>().single()

            assertThat(percentage.id).isEqualTo(4L)
            assertThat(percentage.discount).isEqualTo(30)
            assertThat(percentage.availableTime.start.toString()).isEqualTo("12:00")
            assertThat(percentage.availableTime.end.toString()).isEqualTo("14:00")
        }
}
