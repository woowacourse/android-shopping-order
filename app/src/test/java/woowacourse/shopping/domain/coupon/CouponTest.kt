package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.FIXED
import woowacourse.shopping.fixture.SHOPPING_CART_PRODUCTS_TO_ORDER
import java.time.LocalDate

class CouponTest {
    @Test
    fun `기간이 만료된 쿠폰은 사용할 수 없다`() {
        // given
        val expected = false

        // when
        val actual =
            FIXED.isAvailable(
                SHOPPING_CART_PRODUCTS_TO_ORDER,
                LocalDate.of(2023, 1, 1),
            )

        assertThat(expected).isEqualTo(actual)
    }
}
