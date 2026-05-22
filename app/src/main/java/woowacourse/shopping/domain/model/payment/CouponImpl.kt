package woowacourse.shopping.domain.model.payment

import java.time.LocalDate

class Fixed5000Coupon(
    code: String,
    expirationDate: LocalDate,
) : Coupon(code, expirationDate) {
    override fun doApply(order: Order): Order {
        if (order.items.totalPrice < 100_000) return order
        return order.copy(discountAmount = order.discountAmount + 5_000)
    }
}

class BogoCoupon(
    code: String,
    expirationDate: LocalDate,
) : Coupon(code, expirationDate) {
    override fun doApply(order: Order): Order {
        val cartItems = order.items.getItems()
        val maxPriceApplicableCartItem =
            cartItems.filter { it.quantity >= 3 }.maxByOrNull { it.product.price.amount }
        return order.copy(
            discountAmount =
                order.discountAmount + (
                    maxPriceApplicableCartItem?.product?.price?.amount
                        ?: 0
                ),
        )
    }
}

class FreeShippingCoupon(
    code: String,
    expirationDate: LocalDate,
) : Coupon(code, expirationDate) {
    override fun doApply(order: Order): Order =
        when (order.deliveryLocation) {
            DeliveryLocation.REMOTE -> order.copy(deliveryFee = DeliveryFee(0))
            DeliveryLocation.STANDARD -> order
        }
}
