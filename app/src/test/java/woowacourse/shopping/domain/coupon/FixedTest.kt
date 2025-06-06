package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.FIXED
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER

class FixedTest {
    @Test
    fun `고정 쿠폰의 할인 금액은 고정된 값이다`() {
        // given
        val expected = FIXED.discount

        // when
        val actual =
            FIXED.disCountAmount(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `고정 쿠폰은 최소 금액을 넘기지 않으면 사용이 불가능하다`() {
        // given
        val expected = false

        // when
        val actual =
            FIXED.isAvailable(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }
}
