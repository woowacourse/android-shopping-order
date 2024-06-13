package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.category
import woowacourse.shopping.imageUrl
import woowacourse.shopping.name

class CartItemTest {
    @Test
    fun `1_000원 상품을 4개 장바구니에 담은 경우 총 가격은 4_000원이다`() {
        // given
        val cartItem =
            CartItem(
                id = 0,
                product = Product(0, name, 1_000, imageUrl, category),
                Quantity(4),
            )

        // when
        val actual = cartItem.totalPrice()

        // then
        assertThat(actual).isEqualTo(4_000)
    }
}
