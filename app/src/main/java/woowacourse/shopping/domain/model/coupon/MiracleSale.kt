package woowacourse.shopping.domain.model.coupon

import java.time.LocalDateTime

data class MiracleSale(
    override val coupon: Coupon,
    private val orderAmount: Int = 0,
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
) : CouponState() {
    override fun condition(): String =
        coupon.code.condition.format(
            coupon.availableTime?.start?.hour,
            coupon.availableTime?.end?.hour,
        )

    override fun isValid(): Boolean =
        currentDateTime.toLocalDate() <= coupon.expirationDate && coupon.availableTime?.isAvailableTime(
            currentDateTime.toLocalTime(),
        ) ?: false

    override fun discountAmount(): Int = (orderAmount * DISCOUNT_RATE).toInt()

    companion object {
        private const val DISCOUNT_RATE = 0.3
    }
}
