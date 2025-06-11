package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

class BogoCouponTest {
    @Test
    fun `동일 상품 3개 이상 구매하고 만료일 이전이면 쿠폰 사용이 가능하다`() {
        // given
        val cartItems = listOf(createCartItem(100_000, 3))
        val availableDateTime = LocalDateTime.of(2025, 6, 30, 5, 0)

        // when
        val actual = BOGO_COUPON.isAvailable(cartItems, availableDateTime)

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `동일 상품 3개 미만 구매하면 쿠폰 사용이 불가능하다`() {
        val cartItems: List<ShoppingCart> = listOf(createCartItem(100_000, 2))
        val availableDateTime = LocalDateTime.of(2025, 6, 30, 5, 0)

        // when
        val actual = BOGO_COUPON.isAvailable(cartItems, availableDateTime)

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `만료일 이후면 쿠폰 사용이 불가능하다`() {
        val cartItems: List<ShoppingCart> = listOf(createCartItem(100_000, 2))
        val unavailableDateTime = LocalDateTime.of(2025, 7, 1, 5, 0)

        // when
        val actual = BOGO_COUPON.isAvailable(cartItems, unavailableDateTime)

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `동일 상품 3개 이상이면 한 개의 상품 금액만큼 할인 금액을 반환한다`() {
        val productPrice = 100_000
        val cartItems: List<ShoppingCart> = listOf(createCartItem(productPrice, 3))

        // when
        val actual = BOGO_COUPON.calculateDiscount(cartItems)

        // then
        assertThat(actual).isEqualTo(productPrice)
    }
}
