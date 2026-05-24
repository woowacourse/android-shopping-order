package woowacourse.shopping.model

import java.time.Clock
import java.time.LocalDate

data class Coupon(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val expirationDate: LocalDate,
    val minimumOrderAmount: Int? = null,
    val fixedDiscountAmount: Int? = null,
    val percentageDiscountRate: Int? = null,
    val requiredSameProductQuantity: Int? = null,
    val freeShipping: Boolean = false,
    val bogoEligible: Boolean = false,
    val availableFromHour: Int? = null,
    val availableToHourExclusive: Int? = null,
)

fun Coupon.isApplicableTo(
    selectedCartOrder: SelectedCartOrder,
    clock: Clock,
): Boolean {
    val today = LocalDate.now(clock)
    if (expirationDate.isBefore(today)) return false

    val orderAmount = selectedCartOrder.totalOrderAmount()
    if (minimumOrderAmount != null && orderAmount < minimumOrderAmount) return false

    if (bogoEligible) {
        val requiredQuantity = requiredSameProductQuantity ?: return false
        if (!selectedCartOrder.hasItemQuantityAtLeast(requiredQuantity)) return false
    }

    if (availableFromHour != null && availableToHourExclusive != null) {
        val currentHour = clock.instant().atZone(clock.zone).hour
        if (currentHour !in availableFromHour until availableToHourExclusive) return false
    }

    return true
}

fun Coupon?.discountAmountFor(selectedCartOrder: SelectedCartOrder): Long {
    val coupon = this ?: return 0

    coupon.fixedDiscountAmount?.let { return it.toLong() }
    coupon.percentageDiscountRate?.let { rate ->
        return selectedCartOrder.totalOrderAmount() * rate / 100
    }
    if (coupon.bogoEligible) {
        val requiredQuantity = coupon.requiredSameProductQuantity ?: return 0
        return selectedCartOrder.highestPricedItemAmountWithQuantityAtLeast(requiredQuantity)
    }

    return 0
}

fun Coupon?.deliveryFeeFor(
    orderAmount: Long,
    defaultDeliveryFee: Long,
): Long {
    if (orderAmount <= 0) return 0
    if (this?.freeShipping == true) return 0
    return defaultDeliveryFee
}
