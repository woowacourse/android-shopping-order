package woowacourse.shopping.domain.model.coupon.policy

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

class FreeQuantityDiscountPolicyTest {
    @Test
    fun `무료 제공 수량이 1개이고 2,000원 제품 1개를 구매한다면 2,000원 할인된다`() {
        // given
        val freeQuantityDiscountPolicy =
            FreeQuantityDiscountPolicy(
                discountConditions = listOf(),
                minimumQuantity = 0,
                freeQuantity = 1,
            )
        val cartItems = cartItems(productId = 0, quantity = 3, price = 2_000)

        // when
        val actual = freeQuantityDiscountPolicy.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(2_000)
    }

    @Test
    fun `무료 제공 수량이 1개이고 여러 제품을 구매한다면 가장 비싼 제품만큼 할인된다`() {
        // given
        val freeQuantityDiscountPolicy =
            FreeQuantityDiscountPolicy(
                discountConditions = listOf(),
                minimumQuantity = 0,
                freeQuantity = 1,
            )
        val cartItems =
            cartItems(productId = 0, quantity = 3, price = 1_000) +
                cartItems(productId = 1, quantity = 3, price = 2_000) +
                cartItems(productId = 2, quantity = 3, price = 3_000)

        // when
        val actual = freeQuantityDiscountPolicy.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(3_000)
    }

    private fun cartItems(
        productId: Int,
        quantity: Int,
        price: Int,
    ): List<CartItem> {
        return listOf(
            CartItem(0, Product(productId, "", price, "", ""), Quantity(quantity)),
        )
    }
}
