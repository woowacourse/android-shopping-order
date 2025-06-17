package woowacourse.shopping.domain.model.coupon

class DiscountedAmount(
    val discountRate: Int = 0,
    val discountPrice: Int = 0,
) {
    fun applyDiscount(originalPrice: Int): Int = maxOf(0, (originalPrice - rateDiscountAmount(originalPrice) - discountPrice))

    fun rateDiscountAmount(originalPrice: Int): Int {
        val longResult = (originalPrice.toLong() * discountRate / 100)
        return longResult.toInt()
    }
}
