package woowacourse.shopping.domain.entity.coupon

data class DiscountResult(
    val orderPrice: Long,
    val discountPrice: Long,
    val shippingFee: Long,
) {
    val paymentPrice: Long
        get() = orderPrice - discountPrice + shippingFee
}