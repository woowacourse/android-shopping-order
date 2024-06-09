package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CartDataTest {
    @Test
    fun `장바구니에 담길 상품의 수량을 증가시킬 수 있다`() {
        // given
        val cartData = CartData(1, 1, 0)

        // when
        val actualResult = cartData.increaseQuantity(step = 1)

        // then
        assertThat(actualResult.quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니에 담길 상품의 수량을 감소시킬 수 있다`() {
        // given
        val cartData = CartData(1, 1, 2)

        // when
        val actualResult = cartData.decreaseQuantity(step = 1)

        // then
        assertThat(actualResult.quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니에 담길 상품의 수량은 특정 값보다 작아질 수 없다`() {
        // given
        val cartData = CartData(1, 1, 1)

        // when
        val actualResult = cartData.decreaseQuantity(step = 2)

        // then
        assertThat(actualResult.quantity).isEqualTo(0)
    }

    @Test
    fun `장바구니에 담길 아이템의 총 가격을 알아낼 수 있다`() {
        // given
        val cartData = CartData(1, 1, 2)

        // when
        val actualResult = cartData.totalPrice(1000)

        // then
        assertThat(actualResult).isEqualTo(2000)
    }
}
