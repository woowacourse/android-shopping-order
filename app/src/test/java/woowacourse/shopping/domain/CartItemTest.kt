package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

class CartItemTest {
    @Test
    fun `CartItem의 totalPrice는 수량 곱하기 가격이어야 한다`() {
        // given
        val product =
            Product(
                productId = 1L,
                name = "테스트1",
                _price = Price(10000),
                imageUrl = "",
                category = "커피",
            )
        val quantity = 3

        // when
        val cartItem =
            CartItem(
                product = product,
                quantity = quantity,
            )

        // then
        assertThat(cartItem.totalPrice).isEqualTo(30000)
    }
}
