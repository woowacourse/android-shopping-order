package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.DiscountType
import woowacourse.shopping.fixture.CouponFixture
import woowacourse.shopping.fixture.ProductsFixture
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CouponTest {
    @Test
    fun `만료일이_지난_쿠폰은_유효하지_않은_쿠폰이다`() {
        val expiredCoupon =
            CouponFixture.fixedDummyCoupon.copy(expirationDate = LocalDate.of(2024, 1, 1))
        val result =
            expiredCoupon.isValidForOrder(
                0,
                emptyList(),
                orderTime = LocalDateTime.now(),
            )
        assertThat(result).isFalse()
    }

    @Test
    fun `최소_주문_금액보다_주문금액이_적은_쿠폰은_유효하지_않은_쿠폰이다`() {
        val result =
            CouponFixture.fixedDummyCoupon.isValidForOrder(
                40_000,
                emptyList(),
                orderTime = LocalDateTime.now(),
            )
        assertThat(result).isFalse()
    }

    @Test
    fun `시간_조건이_있을_때_범위_밖이면_유효하지_않은_쿠폰이다`() {
        val timeLimitedCoupon =
            CouponFixture.fixedDummyCoupon.copy(
                availableTime = AvailableTime(LocalTime.of(9, 0), LocalTime.of(11, 0)),
            )
        val result =
            timeLimitedCoupon.isValidForOrder(
                20_000,
                emptyList(),
                orderTime = LocalDateTime.of(2025, 6, 1, 12, 0),
            )
        assertThat(result).isFalse()
    }

    @Test
    fun `BuyXGetY_조건을_충족하지_못하면_유효하지_않은_쿠폰이다`() {
        val buyXGetYCoupon =
            CouponFixture.fixedDummyCoupon.copy(
                discountType = DiscountType.BuyXGetY(buyQuantity = 2, getQuantity = 1),
            )
        val items = ProductsFixture.dummyCartItems
        val result =
            buyXGetYCoupon.isValidForOrder(
                20_000,
                items,
                orderTime = LocalDateTime.of(2025, 6, 1, 10, 0),
            )
        assertThat(result).isFalse()
    }

    @Test
    fun `모든_조건을_만족하면_유효한_쿠폰이다`() {
        val availableCoupon =
            CouponFixture.fixedDummyCoupon.copy(
                availableTime = AvailableTime(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                discountType = DiscountType.BuyXGetY(buyQuantity = 2, getQuantity = 1),
            )
        val items = listOf(ProductsFixture.dummyCartItem.copy(quantity = 3))
        val result =
            availableCoupon.isValidForOrder(
                60_000,
                items,
                orderTime = LocalDateTime.of(2025, 6, 1, 10, 0),
            )
        assertThat(result).isTrue()
    }
}
