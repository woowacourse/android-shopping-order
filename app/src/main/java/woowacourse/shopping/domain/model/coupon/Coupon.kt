package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.OrderInfo
import java.time.LocalDate

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    fun calculateDiscount(orderInfo: OrderInfo): OrderInfo

    fun isAvailable(): Boolean = expirationDate.isAfter(LocalDate.now())
}
