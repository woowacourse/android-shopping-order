package woowacourse.shopping.view.order.model

import woowacourse.shopping.domain.model.coupon.DiscountStrategy
import java.time.LocalDate
import java.time.LocalTime

data class CouponUiModel(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
    val minimumAmount: Int = 0,
    var isSelected: Boolean = false,
    val discount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTimeStart: LocalTime? = null,
    val availableTimeEnd: LocalTime? = null,
    val discountStrategy: DiscountStrategy,
)
