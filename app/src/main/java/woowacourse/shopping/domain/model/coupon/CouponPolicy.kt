package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.cart.SelectedCartOrder

sealed interface CouponPolicy {
    fun isApplicableTo(selectedCartOrder: SelectedCartOrder): Boolean = true

    fun discountAmountFor(selectedCartOrder: SelectedCartOrder): Long = 0

    fun deliveryFeeFor(
        orderAmount: Long,
        defaultDeliveryFee: Long,
    ): Long = defaultDeliveryFee
}

data class OrderFixedAmountDiscountPolicy(
    val amount: Int,
) : CouponPolicy {
    override fun discountAmountFor(selectedCartOrder: SelectedCartOrder): Long = amount.toLong()
}

data class OrderPercentageDiscountPolicy(
    val rate: Int,
) : CouponPolicy {
    override fun discountAmountFor(selectedCartOrder: SelectedCartOrder): Long =
        selectedCartOrder.totalOrderAmount() * rate / 100
}

data class SameProductQuantityDiscountPolicy(
    val requiredSameProductQuantity: Int,
) : CouponPolicy {
    override fun isApplicableTo(selectedCartOrder: SelectedCartOrder): Boolean =
        selectedCartOrder.hasItemQuantityAtLeast(requiredSameProductQuantity)

    override fun discountAmountFor(selectedCartOrder: SelectedCartOrder): Long =
        selectedCartOrder.highestPricedItemAmountWithQuantityAtLeast(requiredSameProductQuantity)
}

object FreeShippingPolicy : CouponPolicy {
    override fun deliveryFeeFor(
        orderAmount: Long,
        defaultDeliveryFee: Long,
    ): Long = 0
}
