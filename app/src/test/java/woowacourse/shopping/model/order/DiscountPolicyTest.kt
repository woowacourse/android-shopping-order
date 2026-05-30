@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.model.order

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.order.discount.BuyXGetYDiscountPolicy
import woowacourse.shopping.model.order.discount.FixedDiscountPolicy
import woowacourse.shopping.model.order.discount.FreeShippingDiscountPolicy
import woowacourse.shopping.model.order.discount.PercentageDiscountPolicy
import woowacourse.shopping.model.product.Money

class DiscountPolicyTest {
    @Test
    fun `Buy X Get Y 정책은 동일 상품을 X+Y개 이상 구매 시 가장 비싼 상품의 Y개 분량을 할인한다`() {
        val policy = BuyXGetYDiscountPolicy(buyQuantity = 2, getQuantity = 1)
        val items =
            listOf(
                OrderItem(productId = 1, price = Money(1000), quantity = 3),
                OrderItem(productId = 2, price = Money(2000), quantity = 3),
                OrderItem(productId = 3, price = Money(3000), quantity = 2),
            )
        val totalProductAmount = Money(15000)
        val shippingFee = Money(3000)

        val discount = policy.calculateDiscount(items, totalProductAmount, shippingFee)

        assertEquals(Money(2000), discount)
    }

    @Test
    fun `Buy X Get Y 정책에서 조건에 맞는 상품이 없으면 0원을 할인한다`() {
        val policy = BuyXGetYDiscountPolicy(buyQuantity = 2, getQuantity = 1)
        val items =
            listOf(
                OrderItem(productId = 1, price = Money(1000), quantity = 2),
            )

        val discount = policy.calculateDiscount(items, Money(2000), Money(3000))

        assertEquals(Money.ZERO, discount)
    }

    @Test
    fun `무료 배송 정책은 최소 주문 금액 이상일 때 배송비를 할인한다`() {
        val policy = FreeShippingDiscountPolicy(minimumAmount = Money(50000))
        val shippingFee = Money(3000)

        assertEquals(shippingFee, policy.calculateDiscount(emptyList(), Money(50000), shippingFee))
        assertEquals(Money.ZERO, policy.calculateDiscount(emptyList(), Money(49999), shippingFee))
    }

    @Test
    fun `고정 할인 정책은 최소 주문 금액 이상일 때 고정 금액을 할인한다`() {
        val policy = FixedDiscountPolicy(discount = Money(5000), minimumAmount = Money(20000))

        assertEquals(Money(5000), policy.calculateDiscount(emptyList(), Money(20000), Money(3000)))
        assertEquals(Money.ZERO, policy.calculateDiscount(emptyList(), Money(19999), Money(3000)))
    }

    @Test
    fun `정률 할인 정책은 전체 상품 금액의 일정 비율을 할인한다`() {
        val policy = PercentageDiscountPolicy(discountPercentage = 10)

        val discount = policy.calculateDiscount(emptyList(), Money(20000), Money(3000))

        assertEquals(Money(2000), discount)
    }
}
