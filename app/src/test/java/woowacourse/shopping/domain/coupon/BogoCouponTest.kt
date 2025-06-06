package woowacourse.shopping.domain.coupon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.fixture.productFixture1
import java.time.LocalDate

class BogoCouponTest {
    private val today = LocalDate.now()

    @Test
    fun `유효 기간이 지났으면 사용 불가능하다`() {
        val coupon =
            BogoCoupon(
                id = 1,
                code = "EXPIRED",
                description = "Expired Coupon",
                discountType = "BOGO",
                expirationDate = today.minusDays(1),
                buyQuantity = 2,
                getQuantity = 1,
            )

        val order =
            listOf(
                ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
            )

        val result = coupon.isUsable(today, order)

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
                expirationDate = today.plusDays(1),
                buyQuantity = 3,
                getQuantity = 1,
            )

        val order =
            listOf(
                ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
            )

        val result = coupon.isUsable(today, order)

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
                expirationDate = today.plusDays(1),
                buyQuantity = 2,
                getQuantity = 1,
            )

        val order =
            listOf(
                ShoppingCart(id = 1, quantity = Quantity(2), product = productFixture1),
                ShoppingCart(id = 1, quantity = Quantity(3), product = productFixture1),
            )

        val result = coupon.isUsable(today, order)

        assertTrue(result)
    }
}
