package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsByTotalPrice
import woowacourse.shopping.domain.model.coupon.Coupon
import java.time.LocalDate

class FreeShippingCouponTest {
    @Test
    fun `쿠폰을 적용할 수 있는 최소 주문 금액이 10,000원이고 주문 금액이 5,000원이라면 적용할 수 없다`() {
        // given
        val freeShippingCoupon = freeShippingCoupon(minimumPrice = 10_000)
        val cartItems = cartItemsByTotalPrice(totalPrice = 5_000)

        // when
        val actual = freeShippingCoupon.available(cartItems)

        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `쿠폰을 적용할 수 있는 최소 주문 금액이 10,000원이고 주문 금액이 20,000원이라면 적용할 수 있다`() {
        // given
        val freeShippingCoupon = freeShippingCoupon(minimumPrice = 10_000)
        val cartItems = cartItemsByTotalPrice(totalPrice = 20_000)

        // when
        val actual = freeShippingCoupon.available(cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `배송비 만큼 할인해준다`() {
        // given
        val freeShippingCoupon = freeShippingCoupon(minimumPrice = 10_000)
        val cartItems = cartItemsByTotalPrice(totalPrice = 20_000)

        // when
        val actual = freeShippingCoupon.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(Coupon.DELIVERY_FEE)
    }

    private fun freeShippingCoupon(
        id: Int = 0,
        expirationDate: LocalDate = LocalDate.of(3000, 10, 10),
        minimumPrice: Int,
    ): FreeShippingCoupon {
        return FreeShippingCoupon(id, "", "", expirationDate, minimumPrice)
    }
}
