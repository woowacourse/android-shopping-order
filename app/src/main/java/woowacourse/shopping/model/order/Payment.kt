package woowacourse.shopping.model.order

import woowacourse.shopping.model.Money

data class Payment(
    val subtotal: Money,
    val couponDiscount: Money,
    val shippingFee: Money,
) {
    val finalAmount: Money get() = subtotal - couponDiscount + shippingFee

    companion object {
        val EMPTY = Payment(
            subtotal = Money(0),
            couponDiscount = Money(0),
            shippingFee = Money(0),
        )
    }
}
