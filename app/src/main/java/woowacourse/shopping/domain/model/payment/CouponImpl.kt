package woowacourse.shopping.domain.model.payment

import java.time.LocalDate
import java.time.LocalTime

class FixedAmountCoupon(
    code: String,
    expirationDate: LocalDate,
    val minimumAmount: Long,
    val discountAmount: Long,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean = order.items.totalPrice >= minimumAmount

    override fun doApply(order: Order): Order =
        order.copy(discountAmount = order.discountAmount + discountAmount)
}

class PercentageCoupon(
    code: String,
    expirationDate: LocalDate,
    val discountRate: Int,
    val availableTimeStart: LocalTime? = null,
    val availableTimeEnd: LocalTime? = null,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean {
        if (availableTimeStart == null || availableTimeEnd == null) return true
        val orderTime = order.dateTime.toLocalTime()
        return orderTime >= availableTimeStart && orderTime <= availableTimeEnd
    }

    override fun doApply(order: Order): Order {
        val discountAmount = (order.items.totalPrice * discountRate / 100.0).toInt()
        return order.copy(discountAmount = order.discountAmount + discountAmount)
    }
}

class BuyXGetYCoupon(
    code: String,
    expirationDate: LocalDate,
    val buyQuantity: Long,
    val freeGetQuantity: Long,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean =
        order.items.getItems().any { it.quantity >= buyQuantity + freeGetQuantity }

    override fun doApply(order: Order): Order {
        val maxPriceApplicableCartItem =
            order.items.getItems()
                .filter { it.quantity >= buyQuantity + freeGetQuantity }
                .maxByOrNull { it.product.price.amount }
        return order.copy(
            discountAmount = order.discountAmount + (
                (maxPriceApplicableCartItem?.product?.price?.amount ?: 0) * freeGetQuantity
            ),
        )
    }
}

class FreeShippingCoupon(
    code: String,
    expirationDate: LocalDate,
    val minimumAmount: Long,
) : Coupon(code, expirationDate) {
    override fun doIsApplicable(order: Order): Boolean =
        order.deliveryLocation == DeliveryLocation.REMOTE && order.items.totalPrice >= minimumAmount

    override fun doApply(order: Order): Order =
        order.copy(deliveryFee = DeliveryFee(0))
}
