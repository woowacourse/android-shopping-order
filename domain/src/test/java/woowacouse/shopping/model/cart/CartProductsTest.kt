package woowacouse.shopping.model.cart

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.product.Product

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
        val actual = cartProducts.addCart(CartProduct(2, 2, "치킨", 15_000, 1, false)).getAll()

        // then
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
        val actual = cartProducts.deleteCart(2L).getAll()

        // then
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
        val actual = cartProducts.updateCartChecked(1L, true).getAll()

        // then
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, true),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이디들로 아이템들의 체크를 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )
        val cartsIds = listOf(1L, 2L)

        // when
        val actual = cartProducts.updateAllCartsChecked(cartsIds, true).getAll()

        // then
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, true),
            CartProduct(2, 2, "치킨", 15_000, 1, true),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 아이템이 2개고, 모든 아이템이 UnChecked 상태라면 총 가격은 0원이다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        val actual = cartProducts.totalPrice
        val expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니에 12_000원, 15_000원 아이템들이 Checked 상태면 총 가격은 27_000원이다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, true),
                CartProduct(2, 2, "치킨", 15_000, 1, true),
            )
        )

        val actual = cartProducts.totalPrice
        val expected = 27_000
        assertEquals(expected, actual)
    }

    companion object {
        fun CartProduct(cartId: Long, productId: Long, productName: String, price: Int, count: Int, checked: Boolean): CartProduct =
            CartProduct(cartId, Product(productId, productName, price, "", count), checked)
    }
}
