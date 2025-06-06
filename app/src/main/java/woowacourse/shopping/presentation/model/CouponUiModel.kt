package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountType
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val discountType: String,
    val minimumAmount: Int?,
    val availableTime: AvailableTimeUiModel?,
    val isSelected: Boolean = false,
)

fun Coupon.toPresentation(): CouponUiModel =
    CouponUiModel(
        code = code,
        description = description,
        expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
        discount = discount,
        discountType = discountType.toPresentation(),
        minimumAmount = minimumAmount,
        availableTime = availableTime?.toPresentation(),
        isSelected = false,
    )

fun DiscountType.toPresentation(): String =
    when (this) {
        is DiscountType.FixedAmount -> "fixed"
        is DiscountType.Percentage -> "percentage"
        is DiscountType.FreeShipping -> "free_shipping"
        is DiscountType.BuyXGetY -> "buyXgetY"
    }
