package woowacouse.shopping.model.cart

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.product.Product

class CartProductsTest {
    @Test
    fun `장바구니 아이디가 2인 아이템을 조회할 수 있다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        val actual = cartProducts.getCartByCartId(2L)
        val expected = CartProduct(2, 2, "치킨", 15_000, 1, false)
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니의 상품 아이디가 1인 아이템을 조회할 수 있다`() {
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        val actual = cartProducts.getCartByProductId(1L)
        val expected = CartProduct(1, 1, "피자", 12_000, 1, false)
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
    fun `장바구니 상품 아이디로 장바구니 아이디를 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(9999, 1, "피자", 12_000, 1, false),
            )
        )

        // when
        val actual = cartProducts.updateCartId(1L, 1L)

        // then
        val expected = 1L
        assertEquals(expected, actual.getAll()[0].id)
    }

    @Test
    fun `장바구니 상품 아이디가 장바구니 상품에 없다면 장바구니 아이디는 변경되지 않는다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(9999, 1, "피자", 12_000, 1, false),
            )
        )

        // when
        val actual = cartProducts.updateCartId(1L, 2L).getAll()[0]

        // then
        val expected = 9999L
        assertEquals(expected, actual.id)
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
    fun `장바구니 아이디가 1인 아이템의 개수를 3으로 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )

        // when
        val actual = cartProducts.updateCartCountByCartId(1L, 3).getAll()

        // then
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 3, false),
            CartProduct(2, 2, "치킨", 15_000, 1, false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 상품 개수를 아이디들을 통해 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1, 1, "피자", 12_000, 1, false),
                CartProduct(2, 2, "치킨", 15_000, 1, false),
            )
        )
        val cartIdsToCount = mapOf(1L to 3, 2L to 5)

        // when
        val actual = cartProducts.updateCartCountByCartIds(cartIdsToCount).getAll()

        // then
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 3, false),
            CartProduct(2, 2, "치킨", 15_000, 5, false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `장바구니 상품 아이디들로 장바구니 아이디들을 변경할 수 있다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(9999, 1, "피자", 12_000, 1, false),
                CartProduct(8888, 2, "치킨", 15_000, 1, false),
            )
        )
        val productIdsToCartIds = mapOf(1L to 1L, 2L to 2L)

        // when
        val actual = cartProducts.updateCartIdsByProductIds(productIdsToCartIds).getAll()

        // then
        val expected = listOf(
            CartProduct(1, 1, "피자", 12_000, 1, false),
            CartProduct(2, 2, "치킨", 15_000, 1, false),
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

    @Test
    fun `장바구니의 아이템을 3개씩 나눠 보여줄 때  장바구니 아이템이 4개면 3개만 보여준다`() {
        // given
        val cartProducts = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, false),
                CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
                CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, false),
                CartProduct(4L, Product(4L, "짜장면", 9_000, ""), 1, false),
            )
        )

        // when
        val actual = cartProducts.getDisplayList(lastDisplayedIndex = 0, displayCountCondition = 3)

        // then
        val expected = 3
        assertEquals(expected, actual.size)
    }

    companion object {
        fun CartProduct(
            cartId: Long,
            productId: Long,
            productName: String,
            price: Int,
            count: Int,
            checked: Boolean
        ): CartProduct =
            CartProduct(cartId, Product(productId, productName, price, ""), count, checked)
    }
}
