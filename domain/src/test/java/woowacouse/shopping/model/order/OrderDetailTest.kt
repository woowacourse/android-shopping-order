package woowacouse.shopping.model.order

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.product.Product

class OrderDetailTest {
    @Test
    fun `주문 내역 날짜를 확인할 수 있다`() {
        val order = dummyOrder

        val actual = order.getOrderDate().toString()
        val expected = "2023-06-03"

        assertEquals(expected, actual)
    }

    @Test
    fun `주문 내역 총 금액은 전체 상품 금액에서 사용한 포인트를 뺀 금액이다`() {
        val order = dummyOrder

        val actual = order.getTotalPrice()
        val expected = 27_000 - 300

        assertEquals(expected, actual)
    }

    companion object {
        private val dummyOrder = OrderDetail(
            1L,
            300,
            100,
            "2023-06-03T09:00",
            listOf(
                CartProduct(1L, Product(1L, "피자", 12_000, ""), 1, false),
                CartProduct(2L, Product(2L, "치킨", 15_000, ""), 1, false),
            )
        )
    }
}
