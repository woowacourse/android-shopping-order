package woowacourse.shopping.data.coupon

import java.time.LocalDateTime

data class Fixed5000(
    override val coupon: Coupon,
    private val orderAmount: Int = 0,
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
) : CouponState() {
    override fun condition(): String = coupon.code.condition.format(coupon.minimumAmount)

    override fun isValid(): Boolean = orderAmount >= coupon.minimumAmount && currentDateTime.toLocalDate() <= coupon.expirationDate

    override fun discountAmount(): Int = DISCOUNT_AMOUNT

    companion object {
        private const val DISCOUNT_AMOUNT = 5000
    }
}
