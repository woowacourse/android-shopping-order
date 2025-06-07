package woowacourse.shopping.domain.model.coupon

class DiscountedAmount(
    val discountRate: Int = 0,
    val discountPrice: Int = 0,
) {
    fun applyDiscount(originalPrice: Int): Int = maxOf(0, (originalPrice * (100 - discountRate) / 100) - discountPrice)
}
