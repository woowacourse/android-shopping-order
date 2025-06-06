package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.BUY_X_GET_Y
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER2

class BuyXGetYTest {
    @Test
    fun `x개 구매시 y개 증정은 x개 이상 구매시 사용할 수 있다`() {
        // given
        val expected = false

        // when
        val actual =
            BUY_X_GET_Y.isAvailable(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )

        // then
        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `x개 구매시 y개 증정은 가장 비싼 품목의 y의 금액만큼 할인된다`() {
        // given
        val expected = SHOPPING_CART_PRODUCTS_TO_ORDER.sortedBy { it.price }[0].price

        // when
        val actual =
            BUY_X_GET_Y.discountAmount(
                SHOPPING_CART_PRODUCTS_TO_ORDER2,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }
}
