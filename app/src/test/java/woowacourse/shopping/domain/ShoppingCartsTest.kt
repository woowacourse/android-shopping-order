package woowacourse.shopping.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.fixture.shoppingCartFixture1
import woowacourse.shopping.fixture.shoppingCartFixture2
import woowacourse.shopping.fixture.shoppingCartFixtures

class ShoppingCartsTest {
    @Test
    fun `모든 장바구니의 id를 반환한다`() {
        val carts =
            ShoppingCarts(
                listOf(
                    shoppingCartFixture1,
                    shoppingCartFixture2,
                ),
            )

        assertEquals(listOf(1L, 2L), carts.cartIds)
    }

    @Test
    fun `모든 장바구니의 결제 금액을 합산한다`() {
        val carts =
            ShoppingCarts(
                listOf(
                    shoppingCartFixture1,
                    shoppingCartFixture2,
                ),
            )

        assertEquals(80_000, carts.totalPayment)
    }

    @Test
    fun `mostExpensiveCartWithStandardQuantity는 기준 수량 이상인 상품 중 가장 비싼 상품의 가격을 반환한다`() {
        val carts = ShoppingCarts(shoppingCartFixtures)

        val result = carts.mostExpensiveCartPriceWithStandardQuantity(3)

        assertEquals(20_000, result)
    }
}
