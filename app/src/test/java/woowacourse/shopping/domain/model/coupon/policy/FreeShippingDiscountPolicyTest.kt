package woowacourse.shopping.domain.model.coupon.policy

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsBySize
import woowacourse.shopping.domain.model.coupon.Coupon

class FreeShippingDiscountPolicyTest {
    @Test
    fun `배송비만큼 할인된다`() {
        // given
        val freeShippingDiscountPolicy = FreeShippingDiscountPolicy(discountConditions = listOf())
        val cartItems = cartItemsBySize(1)

        // when
        val actual = freeShippingDiscountPolicy.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(Coupon.DELIVERY_FEE)
    }
}
