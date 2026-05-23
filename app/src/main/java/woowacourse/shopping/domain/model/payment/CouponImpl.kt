package woowacourse.shopping.domain.model.payment

import java.time.LocalDate

class FixedAmountCoupon(
    code: String,
    expirationDate: LocalDate,
    val minimumAmount: Long,
    val discountAmount: Long,
) : Coupon(code, expirationDate) {
    override fun doApply(order: Order): Order {
        if (order.items.totalPrice < minimumAmount) return order
        return order.copy(discountAmount = order.discountAmount + discountAmount)
    }
}

class PercentageCoupon(
    code: String,
    expirationDate: LocalDate,
    val discountRate: Int,
) : Coupon(code, expirationDate) {
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
    override fun doApply(order: Order): Order {
        val cartItems = order.items.getItems()
        val maxPriceApplicableCartItem =
            cartItems
                .filter { it.quantity >= buyQuantity + freeGetQuantity }
                .maxByOrNull { it.product.price.amount }
        return order.copy(
            discountAmount =
                order.discountAmount + (
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
    override fun doApply(order: Order): Order =
        when (order.deliveryLocation) {
            DeliveryLocation.REMOTE -> {
                if (order.items.totalPrice < minimumAmount) return order
                order.copy(deliveryFee = DeliveryFee(0))
            }
            DeliveryLocation.STANDARD -> order
        }
}
