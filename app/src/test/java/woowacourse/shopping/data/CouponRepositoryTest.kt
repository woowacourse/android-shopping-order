package woowacourse.shopping.data

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.dto.response.coupon.AvailableTimeResponse
import woowacourse.shopping.data.remote.dto.response.coupon.CouponResponse
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime

class CouponRepositoryTest {
    @Test
    fun `쿠폰 목록을 타입별 도메인 쿠폰으로 변환하여 반환한다`() =
        runTest {
            val repository =
                CouponRepositoryImpl(
                    FakeCouponApi(
                        listOf(
                            createFixedCouponResponse(),
                            createPercentageCouponResponse(),
                            createBuyXGetYCouponResponse(),
                            createFreeShippingCouponResponse(),
                        ),
                    ),
                )

            val coupons = repository.getCoupons()

            assertThat(coupons[0]).isEqualTo(
                FixedDiscountCoupon(
                    id = 1,
                    code = "1",
                    description = "1",
                    expirationDate = LocalDate.of(2026, 12, 31),
                    discount = 1_000,
                    minimumAmount = 10_000,
                ),
            )
            assertThat(coupons[1]).isEqualTo(
                PercentageDiscountCoupon(
                    id = 2,
                    code = "2",
                    description = "2",
                    expirationDate = LocalDate.of(2026, 12, 31),
                    discountRate = 10,
                    availableTime =
                        woowacourse.shopping.model.coupon.AvailableTime(
                            start = LocalTime.of(11, 0),
                            end = LocalTime.of(14, 0),
                        ),
                ),
            )
            assertThat(coupons[2]).isEqualTo(
                BuyXGetYCoupon(
                    id = 3,
                    code = "3",
                    description = "3",
                    expirationDate = LocalDate.of(2026, 12, 31),
                    buyQuantity = 1,
                    getQuantity = 1,
                ),
            )
            assertThat(coupons[3]).isEqualTo(
                FreeShippingCoupon(
                    id = 4,
                    code = "4",
                    description = "4",
                    expirationDate = LocalDate.of(2026, 12, 31),
                    minimumAmount = 10_000,
                ),
            )
        }
}

private class FakeCouponApi(
    private val coupons: List<CouponResponse>,
) : CouponApi {
    override suspend fun getCoupons(): List<CouponResponse> = coupons
}

private fun createFixedCouponResponse(): CouponResponse =
    CouponResponse(
        id = 1,
        code = "1",
        description = "1",
        expirationDate = "2026-12-31",
        discountType = "fixed",
        discount = 1_000,
        minimumAmount = 10_000,
    )

private fun createPercentageCouponResponse(): CouponResponse =
    CouponResponse(
        id = 2,
        code = "2",
        description = "2",
        expirationDate = "2026-12-31",
        discountType = "percentage",
        discount = 10,
        availableTime =
            AvailableTimeResponse(
                start = "11:00:00",
                end = "14:00:00",
            ),
    )

private fun createBuyXGetYCouponResponse(): CouponResponse =
    CouponResponse(
        id = 3,
        code = "3",
        description = "3",
        expirationDate = "2026-12-31",
        discountType = "buyXgetY",
        buyQuantity = 1,
        getQuantity = 1,
    )

private fun createFreeShippingCouponResponse(): CouponResponse =
    CouponResponse(
        id = 4,
        code = "4",
        description = "4",
        expirationDate = "2026-12-31",
        discountType = "freeShipping",
        minimumAmount = 10_000,
    )
