package woowacourse.shopping.model.order

import woowacourse.shopping.model.product.Money

interface ShippingPolicy {
    fun calculateShippingFee(totalProductAmount: Money): Money
}

class FixedShippingPolicy(
    private val baseFee: Money = Money(3000),
) : ShippingPolicy {
    override fun calculateShippingFee(totalProductAmount: Money): Money {
        if (totalProductAmount == Money.ZERO) return Money.ZERO
        return baseFee
    }
}
