package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.PERCENTAGE
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER
import java.time.LocalTime

class PercentageTest {
    @Test
    fun `퍼센트 쿠폰은 구매 금액의 해당 퍼센트만큼 할인된다`() {
        // given
        val expected =
            SHOPPING_CART_PRODUCTS_TO_ORDER.sumOf { it.price } * (PERCENTAGE.discount / 100)

        // when
        val actual =
            PERCENTAGE.discountAmount(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `미라클모닝 쿠폰은 오전 4시부터 7시까지만 사용이 가능하다`() {
        // given
        val expected = false

        // when
        val actual =
            PERCENTAGE.isAvailable(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
                now = LocalTime.of(8, 0, 0),
            )
        // then
        assertThat(expected).isEqualTo(actual)
    }
}
