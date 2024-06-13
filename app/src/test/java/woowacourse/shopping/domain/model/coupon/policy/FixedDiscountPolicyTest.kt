package woowacourse.shopping.domain.model.coupon.policy

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsBySize

class FixedDiscountPolicyTest {
    @Test
    fun `할인 금액은 10_000원이다`() {
        // given
        val fixedDiscountPolicy = FixedDiscountPolicy(discountConditions = listOf(), discount = 10_000)
        val cartItems = cartItemsBySize(1)

        // when
        val actual = fixedDiscountPolicy.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(10_000)
    }
}
