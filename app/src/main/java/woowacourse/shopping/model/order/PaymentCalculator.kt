package woowacourse.shopping.model.order

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.coupon.Discount
import java.time.Clock

class PaymentCalculator {
    fun calculate(
        order: Order,
        clock: Clock,
    ): Payment {
        val context = order.couponContext(clock)
        val subtotal = context.subtotal
        return when (val discount = order.selectedCoupon?.discount(context)) {
            is Discount.OnShipping ->
                Payment(
                    subtotal = subtotal,
                    couponDiscount = Money(0),
                    shippingFee = (order.shippingFee - discount.amount).coerceAtLeast(Money(0)),
                )

            is Discount.OnTotal ->
                Payment(
                    subtotal = subtotal,
                    couponDiscount = discount.amount,
                    shippingFee = order.shippingFee,
                )

            null ->
                Payment(
                    subtotal = subtotal,
                    couponDiscount = Money(0),
                    shippingFee = order.shippingFee,
                )
        }
    }
}
