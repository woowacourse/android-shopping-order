package woowacourse.shopping.domain.coupon

data class Discount(
    val productDiscount: Int = 0,
    val shippingDiscount: Int = 0,
) {
    val totalAmount: Int get() = productDiscount + shippingDiscount
}
