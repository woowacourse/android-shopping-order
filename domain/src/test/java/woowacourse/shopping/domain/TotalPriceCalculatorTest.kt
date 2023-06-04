package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class TotalPriceCalculatorTest {

    @Test
    fun `총 금액은 장바구니 아이템의 주문 금액을 합한 것과 같다`() {
        val product1 = Product(1L, "image", "name", 10000)
        val product2 = Product(2L, "image", "name", 20000)
        val product3 = Product(3L, "image", "name", 30000)
        val cartItem1 = CartItem(1L, product1, LocalDateTime.now(), 1)
        val cartItem2 = CartItem(2L, product2, LocalDateTime.now(), 2)
        val cartItem3 = CartItem(3L, product3, LocalDateTime.now(), 3)
        val cartItems = setOf(cartItem1, cartItem2, cartItem3)

        val actual = TotalPriceCalculator.calculate(cartItems)

        assertThat(actual).isEqualTo(140_000)
    }
}
