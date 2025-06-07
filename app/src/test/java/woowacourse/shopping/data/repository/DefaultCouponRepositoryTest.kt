package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeshippingCoupon
import woowacourse.shopping.domain.exception.NetworkResult
import java.time.LocalDate

class DefaultCouponRepositoryTest {
    private val couponDataSource = mockk<CouponDataSource>()
    private lateinit var repository: DefaultCouponRepository

    @BeforeEach
    fun setUp() {
        repository = DefaultCouponRepository(couponDataSource)
    }

    @Test
    fun `쿠폰 목록을 가져온다`() =
        runTest {
            // given
            val expectedCoupons =
                listOf(
                    FixedCoupon(
                        id = 1,
                        code = "FIXED5000",
                        discount = 5000,
                        description = "할인",
                        discountType = "",
                        minimumAmount = 0,
                        expirationDate = LocalDate.now(),
                    ),
                    FreeshippingCoupon(
                        id = 2,
                        code = "FREESHIPPING",
                        minimumAmount = 50000,
                        description = "무료배송",
                        discountType = "",
                        expirationDate = LocalDate.now(),
                    ),
                )
            coEvery { couponDataSource.getCoupons() } returns NetworkResult.Success(expectedCoupons)

            // when
            val result = repository.getCoupons()

            // then
            assertTrue(result is NetworkResult.Success)
            assertEquals(expectedCoupons, (result as NetworkResult.Success).value)
        }
}
