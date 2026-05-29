package woowacourse.shopping.domain.model.order

import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.coupon.Coupon

const val DEFAULT_DELIVERY_FEE = 3_000L

data class OrderPriceSummary(
    val orderAmount: Long,
    val couponDiscount: Long,
    val deliveryFee: Long,
    val totalPaymentPrice: Long,
) {
    companion object {
        fun from(
            order: SelectedCartOrder,
            selectedCoupon: Coupon? = null,
            defaultDeliveryFee: Long = DEFAULT_DELIVERY_FEE,
        ): OrderPriceSummary {
            val orderAmount = order.totalOrderAmount()
            val couponDiscount = selectedCoupon?.discountAmountFor(order)?.coerceAtMost(orderAmount) ?: 0
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
}
