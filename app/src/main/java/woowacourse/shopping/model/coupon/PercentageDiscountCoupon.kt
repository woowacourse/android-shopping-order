package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import java.time.LocalDate
import java.time.LocalTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discountRate: Int,
    val availableTime: AvailableTime,
) : Coupon {
    init {
        require(discountRate >= 0) { "할인율은 음수일 수 없습니다." }
    }

    override fun isValid(payment: Payment): Boolean =
        payment.nowDate <= expirationDate &&
            payment.nowTime in availableTime.start..availableTime.end

    override fun calculateDiscount(payment: Payment): Money {
        if (!isValid(payment)) return Money(0)

        return Money(payment.totalPrice.amount * discountRate / 100)
    }
}

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)
