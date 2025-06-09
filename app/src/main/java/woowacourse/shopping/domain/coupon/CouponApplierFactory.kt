package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts

class CouponApplierFactory {
    fun apply(
        origin: Payment,
        order: ShoppingCarts,
        coupon: Coupon,
    ): Payment {
        return when (coupon) {
            is BogoCoupon -> bogoCouponApplier.apply(origin, order, coupon)
            is FixedCoupon -> fixedCouponApplier.apply(origin, coupon)
            is FreeshippingCoupon -> freeShippingCouponApplier.apply(origin)
            is MiracleSaleCoupon -> miracleSaleCouponApplier.apply(origin, coupon)
        }
    }

    private val bogoCouponApplier =
        OrderBasedCouponApplier<BogoCoupon> { origin, order, coupon ->
            val mostExpensiveProductPrice =
                order.mostExpensiveCartPriceWithStandardQuantity(coupon.buyQuantity)

            val totalPayment = origin.totalPayment - mostExpensiveProductPrice
            origin.copy(
                couponDiscount = -mostExpensiveProductPrice,
                totalPayment = totalPayment,
            )
        }

    private val fixedCouponApplier =
        CouponBasedCouponApplier<FixedCoupon> { origin, coupon ->
            val totalPayment = origin.totalPayment - coupon.discount
            origin.copy(
                couponDiscount = -coupon.discount,
                totalPayment = totalPayment,
            )
        }

    private val freeShippingCouponApplier =
        DefaultCouponApplier<FreeshippingCoupon> { origin ->
            val totalPayment = origin.totalPayment - origin.deliveryFee
            origin.copy(
                couponDiscount = -origin.deliveryFee,
                totalPayment = totalPayment,
            )
        }

    private val miracleSaleCouponApplier =
        CouponBasedCouponApplier<MiracleSaleCoupon> { origin, coupon ->
            val discount = origin.totalPayment * coupon.discount / 100
            val totalPayment = origin.totalPayment - discount

            origin.copy(
                couponDiscount = discount,
                totalPayment = totalPayment,
            )
        }
}
