package woowacourse.shopping.model.order.shipping

import woowacourse.shopping.model.product.Money

class FixedShippingPolicy(
    private val baseFee: Money = Money(3000),
) : ShippingPolicy {
    override fun calculateShippingFee(totalProductAmount: Money): Money {
        if (totalProductAmount == Money.ZERO) return Money.ZERO
        return baseFee
    }
}
