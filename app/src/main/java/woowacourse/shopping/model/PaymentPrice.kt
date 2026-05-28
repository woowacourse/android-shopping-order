package woowacourse.shopping.model

data class PaymentPrice(
    val productTotalPrice: Money,
    val discountAmount: Money = Money(0),
    val isFreeShipping: Boolean = false,
) {
    val shippingFee: Money = if (isFreeShipping) FREE_SHIPPING_FEE else DEFAULT_SHIPPING_FEE

    val finalPrice: Money = Money((productTotalPrice.amount - discountAmount.amount).coerceAtLeast(0) + shippingFee.amount)

    companion object {
        val DEFAULT_SHIPPING_FEE = Money(3000)
        val FREE_SHIPPING_FEE = Money(0)
    }
}
