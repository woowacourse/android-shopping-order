package woowacouse.shopping.model.cart

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.product.Product

class CartProductTest {
    @Test
    fun `장바구니 아이템 체크 상태를 변경할 수 있다`() {
        // given
        val cartProduct =
            CartProduct(1L, 1L, "피자", 12_000, 1, false)

        // when
        val actual = cartProduct.updateCartChecked()

        // then
        val expected = true
        assertEquals(expected, actual.checked)
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
            CartProduct(cartId, Product(productId, productName, price, count), checked)
    }
}
