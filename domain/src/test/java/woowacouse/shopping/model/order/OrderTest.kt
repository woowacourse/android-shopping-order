package woowacouse.shopping.model.order

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.card.Card
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.cart.CartProducts
import woowacouse.shopping.model.point.Point
import woowacouse.shopping.model.product.Product

class OrderTest {
    @Test
    fun `총 주문 금액을 알 수 있다`() {
        val order = Order(
            1L,
            dummyCartProductsThree,
            Point(1_000),
            Card(1L, "우테코은행", "1234-5678-1234-5678", 123)
        )

        val actual = order.getOrderPrice()
        val expected = 55_000
        assertEquals(expected, actual)
    }

    @Test
    fun `최종 결제 금액을 알 수 있다`() {
        val order = Order(
            1L,
            dummyCartProductsThree,
            Point(1_000),
            Card(1L, "우테코은행", "1234-5678-1234-5678", 123)
        )

        val actual = order.getTotalPrice()
        val expected = 54_000
        assertEquals(expected, actual)
    }

    @Test
    fun `결제 카드 정보를 알 수 있다`() {
        val order = Order(
            1L,
            dummyCartProductsThree,
            Point(1_000),
            Card(1L, "우테코은행", "1234-5678-1234-5678", 123)
        )

        val actual = order.getCardNumber()
        val expected = "1234-5678-1234-5678"
        assertEquals(expected, actual)
    }

    companion object {
        private val dummyCartProductsThree = CartProducts(
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, true),
                CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, true),
                CartProduct(3L, Product(3L, "족발", 28_000, ""), 1, true),
            )
        )
    }
}
