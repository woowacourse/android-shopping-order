package woowacouse.shopping.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CartProductsTest {
    @Test
    fun `장바구니 아이디가 1인 아이템을 조회할 수 있다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        val actual = cartProducts.getCart(2L)
        val expected = CartProduct(2, 2, "치킨", 15_000, 1, false)
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이템들을 조회할 수 있다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        val actual = cartProducts.getAll()
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, false),
            CartProduct(2, 2, "치킨", 15_000, 1, false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이템을 추가할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
            )
        )

        // when
        cartProducts.addCart(CartProduct(2, 2, "치킨", 15_000, 1, false))

        // then
        val actual = cartProducts.getAll()
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, false),
            CartProduct(2, 2, "치킨", 15_000, 1, false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이디로 아이템을 삭제할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        // when
        cartProducts.deleteCart(2L)

        // then
        val actual = cartProducts.getAll()
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이디로 아이템의 체크를 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
            )
        )

        // when
        cartProducts.updateCartChecked(1L)

        // then
        val actual = cartProducts.getAll()
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, true),
        )
        assertEquals(expected, actual)
    }

    companion object {
        fun CartProduct(cartId: Long, productId: Long, productName: String, price: Int, count: Int, checked: Boolean): CartProduct =
            CartProduct(cartId, Product(productId, productName, price, count), checked)
    }
}
