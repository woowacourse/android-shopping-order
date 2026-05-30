package woowacourse.shopping.model.order.shipping

import woowacourse.shopping.model.product.Money

interface ShippingPolicy {
    fun calculateShippingFee(totalProductAmount: Money): Money
}
