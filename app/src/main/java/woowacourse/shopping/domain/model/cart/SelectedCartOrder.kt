package woowacourse.shopping.domain.model.cart

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.order.DEFAULT_DELIVERY_FEE
import woowacourse.shopping.domain.model.order.OrderPriceSummary

@Serializable
data class SelectedCartOrder(
    val items: List<SelectedCartOrderItem>,
) {
    fun totalOrderAmount(): Long = items.sumOf(SelectedCartOrderItem::totalPrice)

    fun hasItemQuantityAtLeast(quantity: Int): Boolean = items.any { item -> item.quantity >= quantity }

    fun highestPricedItemAmountWithQuantityAtLeast(quantity: Int): Long =
        items
            .filter { item -> item.quantity >= quantity }
            .maxOfOrNull { item -> item.price.toLong() }
            ?: 0

    fun calculatePriceSummary(
        selectedCoupon: Coupon? = null,
        defaultDeliveryFee: Long = DEFAULT_DELIVERY_FEE,
    ): OrderPriceSummary {
        val orderAmount = totalOrderAmount()
        val couponDiscount = selectedCoupon?.discountAmountFor(this)?.coerceAtMost(orderAmount) ?: 0
        val deliveryFee = selectedCoupon?.deliveryFeeFor(orderAmount, defaultDeliveryFee) ?: defaultDeliveryFee
        val totalPaymentPrice = (orderAmount - couponDiscount + deliveryFee).coerceAtLeast(0)

        return OrderPriceSummary(
            orderAmount = orderAmount,
            couponDiscount = couponDiscount,
            deliveryFee = deliveryFee,
            totalPaymentPrice = totalPaymentPrice,
        )
    }
}

@Serializable
data class SelectedCartOrderItem(
    val cartItemId: Long,
    val productId: Long,
    val price: Int,
    val quantity: Int,
) {
    fun totalPrice(): Long = price.toLong() * quantity
}
