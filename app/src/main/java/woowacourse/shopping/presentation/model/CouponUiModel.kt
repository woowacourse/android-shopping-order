package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Coupon
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
    val availableTime: AvailableTimeUiModel,
    val isSelected: Boolean = false,
)

fun Coupon.toPresentation(): CouponUiModel =
    CouponUiModel(
        code = code,
        description = description,
        expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
        discount = discount,
        minimumAmount = minimumAmount,
        availableTime = availableTime.toPresentation(),
        isSelected = false,
    )
