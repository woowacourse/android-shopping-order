package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class FixedCouponTest {
    @Test
    fun `쿠폰이 만료되었으면 false를 반환한다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 1,
                code = "FIXED1000",
                description = "1000원 할인",
                discountType = "fixed",
                expirationDate = LocalDate.of(2024, 12, 31),
                minimumAmount = 10000,
                discount = 1000,
            )

        // when
        val result =
            coupon.isUsable(
                standardDate = LocalDate.of(2025, 1, 1),
                standardAmount = 20000,
            )

        // then
        assertFalse(result)
    }

    @Test
    fun `최소 주문 금액을 만족하지 않으면 false를 반환한다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 1,
                code = "FIXED1000",
                description = "1000원 할인",
                discountType = "fixed",
                expirationDate = LocalDate.of(2025, 12, 31),
                minimumAmount = 10000,
                discount = 1000,
            )

        // when
        val result =
            coupon.isUsable(
                standardDate = LocalDate.of(2025, 6, 6),
                standardAmount = 9999,
            )

        // then
        assertFalse(result)
    }

    @Test
    fun `유효 기간 이내이고 최소 금액 이상이면 true를 반환한다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 1,
                code = "FIXED1000",
                description = "1000원 할인",
                discountType = "fixed",
                expirationDate = LocalDate.of(2025, 12, 31),
                minimumAmount = 10000,
                discount = 1000,
            )

        // when
        val result =
            coupon.isUsable(
                standardDate = LocalDate.of(2025, 6, 6),
                standardAmount = 15000,
            )

        // then
        assertTrue(result)
    }

    @Test
    fun `만료일과 정확히 같은 날짜에 최소 금액 만족 시 true를 반환한다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 1,
                code = "FIXED1000",
                description = "1000원 할인",
                discountType = "fixed",
                expirationDate = LocalDate.of(2025, 6, 6),
                minimumAmount = 10000,
                discount = 1000,
            )

        // when
        val result =
            coupon.isUsable(
                standardDate = LocalDate.of(2025, 6, 6),
                standardAmount = 10000,
            )

        // then
        assertTrue(result)
    }
}
