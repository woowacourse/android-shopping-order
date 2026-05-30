package woowacourse.shopping.model.order.discount

import woowacourse.shopping.model.order.OrderItem
import woowacourse.shopping.model.product.Money

class BuyXGetYDiscountPolicy(
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : DiscountPolicy {
    override fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money {
        val totalNeeded = buyQuantity + getQuantity
        val targetItem =
            items
                .filter { it.quantity >= totalNeeded }
                .maxByOrNull { it.price } ?: return Money.ZERO

        return targetItem.price * getQuantity
    }
}
