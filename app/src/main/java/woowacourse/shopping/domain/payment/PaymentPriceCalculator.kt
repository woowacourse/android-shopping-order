package woowacourse.shopping.domain.payment

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponBenefit
import woowacourse.shopping.domain.model.PaymentPricingPolicy
import woowacourse.shopping.domain.model.ShoppingCartItem
import java.time.Clock
import java.time.LocalTime

class PaymentPriceCalculator(
    private val paymentPricingPolicy: PaymentPricingPolicy,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    fun calculate(
        items: List<ShoppingCartItem>,
        coupon: Coupon?,
    ): PaymentPriceSummary {
        val subtotalPrice = items.sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
        val couponDiscountPrice =
            calculateCouponDiscount(
                coupon = coupon,
                items = items,
                subtotalPrice = subtotalPrice,
            )
        val deliveryPrice = calculateDeliveryPrice(coupon = coupon, subtotalPrice = subtotalPrice)
        val totalPrice = (subtotalPrice - couponDiscountPrice).coerceAtLeast(0) + deliveryPrice
        return PaymentPriceSummary(
            subtotalPrice = subtotalPrice,
            couponDiscountPrice = couponDiscountPrice,
            deliveryPrice = deliveryPrice,
            totalPrice = totalPrice,
        )
    }

    private fun calculateCouponDiscount(
        coupon: Coupon?,
        items: List<ShoppingCartItem>,
        subtotalPrice: Int,
    ): Int {
        if (coupon == null || items.isEmpty()) return 0

        return when (val benefit = coupon.benefit) {
            is CouponBenefit.AmountDiscount ->
                if (subtotalPrice >= benefit.minimumOrderAmount) {
                    benefit.discountAmount
                } else {
                    0
                }

            is CouponBenefit.BuyTwoGetOne -> {
                val requiredQuantity = benefit.requiredQuantity + benefit.freeQuantity
                val highestEligibleProductPrice =
                    items
                        .filter { shoppingCartItem -> shoppingCartItem.getQuantity() >= requiredQuantity }
                        .maxOfOrNull { shoppingCartItem -> shoppingCartItem.product.getPrice() }

                if (highestEligibleProductPrice == null) {
                    0
                } else {
                    highestEligibleProductPrice * benefit.freeQuantity
                }
            }

            is CouponBenefit.FreeShipping -> 0

            is CouponBenefit.MorningDiscount ->
                if (isCurrentTimeWithin(start = benefit.startTime, end = benefit.endTime)) {
                    (subtotalPrice * benefit.discountRate) / paymentPricingPolicy.percentDenominator
                } else {
                    0
                }

            is CouponBenefit.Unknown -> 0
        }.coerceAtLeast(0)
    }

    private fun calculateDeliveryPrice(
        coupon: Coupon?,
        subtotalPrice: Int,
    ): Int {
        if (subtotalPrice <= 0) return 0

        val benefit = coupon?.benefit
        if (benefit is CouponBenefit.FreeShipping && subtotalPrice >= benefit.minimumOrderAmount) {
            return 0
        }
        return paymentPricingPolicy.defaultDeliveryPrice
    }

    private fun isCurrentTimeWithin(
        start: String,
        end: String,
    ): Boolean {
        val startTime = runCatching { LocalTime.parse(start) }.getOrNull() ?: return false
        val endTime = runCatching { LocalTime.parse(end) }.getOrNull() ?: return false

        val currentTime = LocalTime.now(clock)
        return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime)
    }

    data class PaymentPriceSummary(
        val subtotalPrice: Int,
        val couponDiscountPrice: Int,
        val deliveryPrice: Int,
        val totalPrice: Int,
    )
}
