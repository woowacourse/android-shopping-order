package woowacourse.shopping.domain.model.coupon.condition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsByProductQuantity

class QuantityDiscountConditionTest {
    @Test
    fun `최소 구매 개수가 3개이고 1개의 상품을 5개 구매한다면 쿠폰을 적용할 수 있다`() {
        // given
        val quantityDiscountCondition = QuantityDiscountCondition(3)
        val cartItems = cartItemsByProductQuantity(5)

        // when
        val actual = quantityDiscountCondition.available(cartItems = cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `최소 구매 개수가 3개이고 3개의 상품을 1개씩 구매한다면 쿠폰을 적용할 수 없다`() {
        // given
        val quantityDiscountCondition = QuantityDiscountCondition(3)
        val cartItems = cartItemsByProductQuantity(1, 1, 1)

        // when
        val actual = quantityDiscountCondition.available(cartItems = cartItems)

        // then
        assertThat(actual).isFalse
    }
}
