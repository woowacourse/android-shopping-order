package woowacourse.shopping.model.order.discount

import woowacourse.shopping.model.order.OrderItem
import woowacourse.shopping.model.product.Money

class FixedDiscountPolicy(
    private val discount: Money,
    private val minimumAmount: Money,
) : DiscountPolicy {
    override fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money {
        if (totalProductAmount < minimumAmount) return Money.ZERO
        return discount
    }
}
