package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CartItemDomainTest {
    @Test
    fun `장바구니에 담겨져 있는 아이템의 수량을 증가시킬 수 있다`() {
        // given
        val cartItem = getCartItem(2, 1000)

        // when
        val actualResult = cartItem.plusQuantity()

        // then
        assertThat(actualResult.quantity).isEqualTo(3)
    }

    @Test
    fun `장바구니에 담겨져 있는 아이템의 수량을 감소시킬 수 있다`() {
        // given
        val cartItem = getCartItem(2, 1000)

        // when
        val actualResult = cartItem.minusQuantity()

        // then
        assertThat(actualResult.quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니에 담겨져 있는 아이템의 수량은 정해진 수량 이하로 감소시킬 수 없다`() {
        // given
        val cartItem = getCartItem(1, 1000)

        // when
        val actualResult = cartItem.minusQuantity()

        // then
        assertThat(actualResult.quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니에 담겨져 있는 아이템의 총 가격을 구할 수 있다`() {
        // given
        val cartItem = getCartItem(5, 5000)

        // when
        val actualResult = cartItem.totalPrice()

        // then
        assertThat(actualResult).isEqualTo(25000)
    }

    private fun getCartItem(
        quantity: Int,
        price: Int,
    ): CartItemDomain =
        CartItemDomain(
            cartItemId = 1,
            quantity = quantity,
            product = getProductItemWithDefinedPrice(price),
        )

    private fun getProductItemWithDefinedPrice(price: Int): ProductItemDomain =
        ProductItemDomain(
            id = 1,
            category = "fashion",
            imageUrl = "image",
            name = "nike",
            price = price,
        )
}
