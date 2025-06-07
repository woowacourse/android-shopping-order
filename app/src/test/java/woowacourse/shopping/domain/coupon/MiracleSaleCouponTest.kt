package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDateTime

class MiracleSaleCouponTest {
    @Test
    fun `사용 가능 시간 이내이고 만료일 이전이면 쿠폰 사용이 가능하다`() {
        // given
        val cartItems = listOf(createCartItem(100_000,3))

        //when
        val actual = MIRACLE_SALE_COUPON.isAvailable(cartItems, LocalDateTime.of(2025,6,30,5,0))

        //then
        assertThat(actual).isTrue()
    }

    @Test
    fun `사용 불가능 시간이면 쿠폰 사용이 불가능하다`() {
        val cartItems: List<ShoppingCart> = listOf(createCartItem(100, 2))

        //when
        val actual = MIRACLE_SALE_COUPON.isAvailable(cartItems, LocalDateTime.of(2025,6,30,10,0))

        //then
        assertThat(actual).isFalse()
    }

    @Test
    fun `만료일 이후면 쿠폰 사용이 불가능하다`() {
        val cartItems: List<ShoppingCart> = listOf(createCartItem(100_000,2))

        //when
        val actual = MIRACLE_SALE_COUPON.isAvailable(cartItems, LocalDateTime.of(2025,7,1,5,0))

        //then
        assertThat(actual).isFalse()
    }

    @Test
    fun `쿠폰을 사용하면 할인 금액만큼 할인된다`() {
        val cartItems: List<ShoppingCart> = listOf(createCartItem(100_000, 3))

        //when
        val actual = MIRACLE_SALE_COUPON.calculateDiscount(cartItems)

        //then
        assertThat(actual).isEqualTo(90_000)
    }
}
