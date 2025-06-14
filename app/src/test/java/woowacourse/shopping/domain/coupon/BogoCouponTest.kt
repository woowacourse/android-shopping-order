package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.fixture.productFixture1
import woowacourse.shopping.fixture.shoppingCartFixtures
import java.time.LocalDateTime

class BogoCouponTest {
    private val today = LocalDateTime.now()
    private val previousDay = today.toLocalDate().minusDays(1)
    private val nextDay = today.toLocalDate().plusDays(1)

    @Test
    fun `유효 기간이 지났으면 사용 불가능하다`() {
        val coupon =
            BogoCoupon(
                id = 1,
                code = "EXPIRED",
                description = "Expired Coupon",
                discountType = "BOGO",
                expirationDate = previousDay,
                buyQuantity = 2,
                getQuantity = 1,
            )

        val order =
            ShoppingCarts(
                listOf(
                    ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
                ),
            )

        val result = coupon.isUsable(today, order, 0)

        assertFalse(result)
    }

    @Test
    fun `상품 수량이 부족하면 사용 불가능하다`() {
        val coupon =
            BogoCoupon(
                id = 1,
                code = "NOTENOUGH",
                description = "Not Enough Quantity",
                discountType = "BOGO",
                expirationDate = nextDay,
                buyQuantity = 3,
                getQuantity = 1,
            )

        val order =
            ShoppingCarts(
                listOf(
                    ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
                ),
            )

        val result = coupon.isUsable(today, order, 0)

        assertFalse(result)
    }

    @Test
    fun `여러 상품 중 하나라도 조건을 만족하면 사용 가능하다`() {
        val coupon =
            BogoCoupon(
                id = 1,
                code = "MULTIPLE",
                description = "Multiple Cart Items",
                discountType = "BOGO",
                expirationDate = nextDay,
                buyQuantity = 2,
                getQuantity = 1,
            )

        val order =
            ShoppingCarts(
                listOf(
                    ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
                    ShoppingCart(id = 1, quantity = Quantity(3), product = productFixture1),
                ),
            )

        val result = coupon.isUsable(today, order, 0)

        assertTrue(result)
    }

    @Test
    fun `조건에 맞는 가장 비싼 상품의 가격만큼 할인된다`() {
        // given
        val coupon =
            BogoCoupon(
                id = 1,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = nextDay,
                buyQuantity = 2,
                getQuantity = 1,
                discountType = "BOGO",
            )
        val originPayment =
            Payment(
                totalPayment = 40_000,
                originPayment = 0,
                deliveryFee = 3_000,
                couponDiscount = 0,
            )

        val mostExpensiveProductPrice =
            ShoppingCarts(shoppingCartFixtures)
                .mostExpensiveCartPriceWithStandardQuantity(3)

        // when
        val appliedPayment = coupon.applyToPayment(originPayment, ShoppingCarts(shoppingCartFixtures))

        // then
        assertEquals(-mostExpensiveProductPrice, appliedPayment.couponDiscount)
        assertEquals(
            originPayment.totalPayment - mostExpensiveProductPrice,
            appliedPayment.totalPayment,
        )
    }
}
