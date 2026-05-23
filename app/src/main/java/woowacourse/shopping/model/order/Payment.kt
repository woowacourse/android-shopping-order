package woowacourse.shopping.model.order

import woowacourse.shopping.model.Money

data class Payment(
    val subtotal: Money,
    val couponDiscount: Money,
    val shippingFee: Money,
) {
    val finalAmount: Money get() = subtotal - couponDiscount + shippingFee
}
