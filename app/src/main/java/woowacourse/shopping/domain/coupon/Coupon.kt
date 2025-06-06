package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val code: String
    abstract val explanationDate: LocalDate
    abstract val discountType: DiscountType
    open val minimumAmount: Int? = null
    open val availableStartTime: LocalTime? = null
    open val availableEndTime: LocalTime? = null
}
