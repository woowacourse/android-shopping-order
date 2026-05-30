package woowacourse.shopping.model.order.discount

import woowacourse.shopping.model.order.OrderItem
import woowacourse.shopping.model.product.Money

interface DiscountPolicy {
    fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money
}
