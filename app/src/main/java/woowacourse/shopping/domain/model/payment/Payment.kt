package woowacourse.shopping.domain.model.payment

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponInfos
import woowacourse.shopping.domain.model.coupon.CouponContext
import woowacourse.shopping.domain.model.order.Order

private const val DEFAULT_DELIVERY_FEE = 3_000

data class Payment(
    val order: Order = Order(),
    val selectedCoupon: Coupon? = CouponInfos.defaultCoupons.firstOrNull(),
    val defaultDeliveryFee: Int = DEFAULT_DELIVERY_FEE,
    val couponUseContext: CouponContext = CouponContext(),
) {
    val orderAmount: Int
        get() = order.orderAmount

    val couponDiscountAmount: Int
        get() = minOf(selectedCoupon?.discountAmount(order, couponUseContext) ?: 0, orderAmount)

    val deliveryFee: Int
        get() = selectedCoupon?.deliveryFee(order, defaultDeliveryFee, couponUseContext) ?: defaultDeliveryFee

    val totalPaymentAmount: Int
        get() = orderAmount - couponDiscountAmount + deliveryFee
}
