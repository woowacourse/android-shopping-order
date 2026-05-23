package woowacourse.shopping.model.order

import woowacourse.shopping.model.product.Money

interface ShippingPolicy {
    fun calculateShippingFee(totalProductAmount: Money): Money
}

class FixedShippingPolicy(
    private val baseFee: Money,
) : ShippingPolicy {
    override fun calculateShippingFee(totalProductAmount: Money): Money = baseFee
}
