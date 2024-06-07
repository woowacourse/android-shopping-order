package woowacourse.shopping.data.coupon

import java.time.LocalDateTime

data class Freeshipping(
    override val coupon: Coupon,
    private val orderAmount: Int = 0,
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
) : CouponState() {
    override fun condition(): String = coupon.code.condition.format(coupon.minimumAmount)

    override fun isValid(): Boolean = orderAmount >= coupon.minimumAmount && currentDateTime.toLocalDate() <= coupon.expirationDate

    override fun discountAmount(): Int = DELIVERY_AMOUNT

    companion object {
        const val DELIVERY_AMOUNT = 3000
    }
}
