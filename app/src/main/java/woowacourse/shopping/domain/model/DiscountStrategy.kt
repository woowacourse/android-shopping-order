package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed interface DiscountStrategy {
    fun isApplicable(): Boolean

    fun calculateDiscountAmount(): Int
}

class FixedDiscountStrategy(
    private val orderedItems: List<Cart>,
    private val discountAmount: Int,
    private val minimumAmount: Int,
) : DiscountStrategy {
    override fun isApplicable(): Boolean {
        return orderedItems.sumOf { it.calculatedPrice } >= minimumAmount
    }

    override fun calculateDiscountAmount(): Int {
        return discountAmount
    }
}

class BuyXGetYDiscountStrategy(
    private val orderedItems: List<Cart>,
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : DiscountStrategy {
    override fun isApplicable(): Boolean {
        return orderedItems.any { it.quantity >= (buyQuantity + getQuantity) }
    }

    override fun calculateDiscountAmount(): Int {
        val applicableItems = orderedItems.filter { it.quantity >= (buyQuantity + getQuantity) }
        return applicableItems.maxOf { it.product.price }.toInt()
    }
}

class FreeShippingDiscountStrategy(
    private val orderedItems: List<Cart>,
    private val shippingFee: Int,
    private val minimumAmount: Int,
) : DiscountStrategy {
    override fun isApplicable(): Boolean {
        return orderedItems.sumOf { it.calculatedPrice } >= minimumAmount
    }

    override fun calculateDiscountAmount(): Int {
        return shippingFee
    }
}

class PercentageDiscountStrategy(
    private val orderedItems: List<Cart>,
    private val discountPercentage: Int,
    private val availableTime: AvailableTime,
    private val orderedTime: LocalTime,
) : DiscountStrategy {
    override fun isApplicable(): Boolean {
        return availableTime.isWithinTimeRange(orderedTime)
    }

    override fun calculateDiscountAmount(): Int {
        val totalAmount = orderedItems.sumOf { it.calculatedPrice }
        return totalAmount * discountPercentage / 100
    }
}
