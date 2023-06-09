package woowacourse.shopping.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartProductsTest {

    private lateinit var cartProducts: CartProducts

    @Before
    fun setup() {
        cartProducts = CartProducts(
            listOf(
                CartProduct(
                    id = 1,
                    quantity = 1,
                    product = Product(
                        id = 1,
                        name = "상품1",
                        price = 1500,
                        imageUrl = "",
                    ),
                ),
                CartProduct(
                    id = 2,
                    quantity = 2,
                    product = Product(
                        id = 2,
                        name = "상품2",
                        price = 1000,
                        imageUrl = "",
                    ),
                ),
            ),
        )
    }

    @Test
    fun `장바구니에 담긴 아이템들의 개수는 2개이다`() {
        assertEquals(2, cartProducts.size)
    }

    @Test
    fun `장바구니에 담긴 아이템들의 전체 수량은 3개이다`() {
        assertEquals(3, cartProducts.totalQuantity)
    }

    @Test
    fun `장바구니에 담긴 아이템들의 전체 가격은 3500원이다`() {
        assertEquals(3500, cartProducts.totalPrice)
    }

    @Test
    fun `장바구니에 담긴 아이템들은 우선 모두 체크된다`() {
        assertTrue(cartProducts.totalCheckedPrice == cartProducts.totalPrice)
    }

    @Test
    fun `선택한 아이템들의 전체 수량은 1개이다`() {
        cartProducts.changeChecked(1, true)
        cartProducts.changeChecked(2, false)

        assertEquals(1, cartProducts.totalCheckedQuantity)
    }

    @Test
    fun `선택한 아이템들의 전체 가격은 1500원이다`() {
        cartProducts.changeChecked(1, true)
        cartProducts.changeChecked(2, false)

        assertEquals(1500, cartProducts.totalCheckedPrice)
    }

    @Test
    fun `상품 아이디를 통해 장바구니에서 아이템을 찾을 수 있다`() {
        val productId = 1
        val actual = CartProduct(
            id = 1,
            quantity = 1,
            product = Product(
                id = 1,
                name = "상품1",
                price = 1500,
                imageUrl = "",
            ),
        )

        assertEquals(actual, cartProducts.findByProductId(productId))
    }

    @Test
    fun `상품 아이디가 장바구니에 없는 경우 null을 반환한다`() {
        val productId = 3

        assertEquals(null, cartProducts.findByProductId(productId))
    }

    @Test
    fun `해당 상품이 체크 되어 있는 경우 true를 반환한다`() {
        val product = CartProduct(
            id = 1,
            quantity = 1,
            product = Product(
                id = 1,
                name = "상품1",
                price = 1500,
                imageUrl = "",
            ),
        )

        cartProducts.changeChecked(product.id, true)

        assertTrue(cartProducts.getCheckedState(product))
    }

    @Test
    fun `해당 상품이 체크 되어 있지 않은 경우 false를 반환한다`() {
        val product = CartProduct(
            id = 1,
            quantity = 1,
            product = Product(
                id = 1,
                name = "상품1",
                price = 1500,
                imageUrl = "",
            ),
        )

        cartProducts.changeChecked(product.id, false)

        assertFalse(cartProducts.getCheckedState(product))
    }
}
