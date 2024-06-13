package woowacourse.shopping.domain.model.coupon.policy

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsByTotalPrice

class PercentDiscountPolicyTest {
    @Test
    fun `할인율이 50%이고 주문 금액이 40,000원라면 할인 금액은 20,000원이다`() {
        // given
        val percentDiscountPolicy = PercentDiscountPolicy(discountConditions = listOf(), percent = 50)
        val cartItems = cartItemsByTotalPrice(40_000)

        // when
        val actual = percentDiscountPolicy.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(20_000)
    }
}
