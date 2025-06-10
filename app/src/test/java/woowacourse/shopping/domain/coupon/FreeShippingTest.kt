package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.shoppingCart.DefaultShippingRule
import woowacourse.shopping.fixture.FREE_SHIPPING
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER

class FreeShippingTest {
    @Test
    fun `배송비 무료 쿠폰은 배송비만큼 할인된다`() {
        // given
        val shoppingRule = DefaultShippingRule()
        val expected = shoppingRule.shippingFee

        // when
        val actual =
            FREE_SHIPPING.discountAmount(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `배송비 무료 쿠폰은 최소 금액을 넘기지 못하면 사용할 수 없다`() {
        // given
        val expected = false

        // when
        val actual =
            FREE_SHIPPING.isAvailable(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }
}
