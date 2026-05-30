package woowacourse.shopping.model.order.discount

import woowacourse.shopping.model.order.OrderItem
import woowacourse.shopping.model.product.Money

class PercentageDiscountPolicy(
    private val discountPercentage: Int,
) : DiscountPolicy {
    override fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money = Money(totalProductAmount.value * discountPercentage / 100)
}
