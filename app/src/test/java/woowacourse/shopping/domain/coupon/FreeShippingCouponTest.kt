package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

class FreeShippingCouponTest {
    @Test
    fun `최소 구매 금액 이상이고 만료일 이전이면 쿠폰 사용이 가능하다`() {
        // given
        val availableAmount = 50_000
        val cartItems = listOf(createCartItem(availableAmount, 1))
        val availableDateTime = LocalDateTime.of(2025, 6, 30, 5, 0)

        // when
        val actual = FREE_SHIPPING_COUPON.isAvailable(cartItems, availableDateTime)

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `최소 구매 금액 미만이면 쿠폰 사용이 불가능하다`() {
        val unavailableAmount = 49_999
        val cartItems: List<ShoppingCart> = listOf(createCartItem(unavailableAmount, 1))
        val availableDateTime = LocalDateTime.of(2025, 6, 30, 5, 0)

        // when
        val actual = FREE_SHIPPING_COUPON.isAvailable(cartItems, availableDateTime)

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `만료일 이후면 쿠폰 사용이 불가능하다`() {
        val availableAmount = 50_000
        val cartItems: List<ShoppingCart> = listOf(createCartItem(availableAmount, 2))
        val unavailableDateTime = LocalDateTime.of(2025, 7, 1, 5, 0)

        // when
        val actual = FREE_SHIPPING_COUPON.isAvailable(cartItems, unavailableDateTime)

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `쿠폰을 사용하면 배송비만큼 할인 금액만큼 할인된다`() {
        val availableAmount = 50_000
        val cartItems: List<ShoppingCart> = listOf(createCartItem(availableAmount, 3))
        val shippingPrice = 3_000

        // when
        val actual = FREE_SHIPPING_COUPON.calculateDiscount(cartItems)

        // then
        assertThat(actual).isEqualTo(shippingPrice)
    }
}
