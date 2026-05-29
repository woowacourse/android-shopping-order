package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import java.time.Clock
import java.time.LocalDate

data class Coupon(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val expirationDate: LocalDate,
    val minimumOrderAmount: Int? = null,
    val availableFromHour: Int? = null,
    val availableToHourExclusive: Int? = null,
    val policy: CouponPolicy,
) {
    fun isApplicableTo(
        selectedCartOrder: SelectedCartOrder,
        clock: Clock,
    ): Boolean {
        val today = LocalDate.now(clock)
        if (expirationDate.isBefore(today)) return false

        val orderAmount = selectedCartOrder.totalOrderAmount()
        if (minimumOrderAmount != null && orderAmount < minimumOrderAmount) return false

        if (availableFromHour != null && availableToHourExclusive != null) {
            val currentHour = clock.instant().atZone(clock.zone).hour
            if (currentHour !in availableFromHour until availableToHourExclusive) return false
        }

        return policy.isApplicableTo(selectedCartOrder)
    }

    fun discountAmountFor(selectedCartOrder: SelectedCartOrder): Long = policy.discountAmountFor(selectedCartOrder)

    fun deliveryFeeFor(
        orderAmount: Long,
        defaultDeliveryFee: Long,
    ): Long {
        if (orderAmount <= 0) return 0
        return policy.deliveryFeeFor(orderAmount, defaultDeliveryFee)
    }
}
