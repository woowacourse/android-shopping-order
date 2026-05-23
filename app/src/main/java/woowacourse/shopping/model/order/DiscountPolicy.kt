package woowacourse.shopping.model.order

import woowacourse.shopping.model.product.Money

interface DiscountPolicy {
    fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money
}

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

class PercentageDiscountPolicy(
    private val discountPercentage: Int,
) : DiscountPolicy {
    override fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money = Money(totalProductAmount.value * discountPercentage / 100)
}

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

class FreeShippingDiscountPolicy(
    private val minimumAmount: Money,
) : DiscountPolicy {
    override fun calculateDiscount(
        items: List<OrderItem>,
        totalProductAmount: Money,
        shippingFee: Money,
    ): Money {
        if (totalProductAmount < minimumAmount) return Money.ZERO
        return shippingFee
    }
}
