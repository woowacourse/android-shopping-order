package woowacourse.shopping.domain.entity.coupon

data class DiscountResult(
    val orderPrice: Long,
    val discountPrice: Long,
    val shippingFee: Long,
) {
    val paymentPrice: Long
        get() {
            val price = orderPrice - discountPrice + shippingFee
            require(price >= 0) { "결제 금액은 0원 이상이어야 합니다." }
            return price
        }
}
