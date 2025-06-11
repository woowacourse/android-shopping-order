package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.fixture.shoppingCartFixture1
import java.time.LocalDate
import java.time.LocalDateTime

class FreeshippingCouponResponseTest {
    @Test
    fun `쿠폰이 만료되었으면 false를 반환한다`() {
        // given
        val coupon =
            FreeshippingCoupon(
                id = 1,
                code = "FREE2024",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2024, 12, 31),
                minimumAmount = 10000,
            )

        // when
        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                ShoppingCarts(listOf(shoppingCartFixture1)),
                15000,
            )

        // then
        assertFalse(result)
    }

    @Test
    fun `최소 주문 금액 미만이면 false를 반환한다`() {
        // given
        val coupon =
            FreeshippingCoupon(
                id = 1,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 12, 31),
                minimumAmount = 10000,
            )

        // when
        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 10, 0, 0),
                ShoppingCarts(listOf(shoppingCartFixture1)),
                9999,
            )

        // then
        assertFalse(result)
    }

    @Test
    fun `유효 기간 이내이고 최소 금액 이상이면 true를 반환한다`() {
        // given
        val coupon =
            FreeshippingCoupon(
                id = 1,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 12, 31),
                minimumAmount = 10000,
            )

        // when
        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 10, 0, 0),
                ShoppingCarts(listOf(shoppingCartFixture1)),
                15000,
            )

        // then
        assertTrue(result)
    }

    @Test
    fun `만료일 당일이고 최소 금액이 정확히 일치하면 true를 반환한다`() {
        // given
        val coupon =
            FreeshippingCoupon(
                id = 1,
                code = "",
                description = "",
                discountType = "",
                expirationDate = LocalDate.of(2025, 6, 6),
                minimumAmount = 10000,
            )

        // when
        val result =
            coupon.isUsable(
                LocalDateTime.of(2025, 6, 6, 10, 0, 0),
                ShoppingCarts(listOf(shoppingCartFixture1)),
                10000,
            )

        // then
        assertTrue(result)
    }
}
