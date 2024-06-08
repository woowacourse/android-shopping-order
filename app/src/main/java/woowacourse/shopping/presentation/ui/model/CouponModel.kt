package woowacourse.shopping.presentation.ui.model

import woowacourse.shopping.domain.model.Coupon
import java.time.format.DateTimeFormatter

data class CouponModel(
    val id: Long,
    val description: String,
    val expiredDate: String,
    val minimumAmount: String,
    val discountAmount: Int,
)

fun Coupon.toUiModel(): CouponModel {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    return CouponModel(
        id = id.toLong(),
        description = description,
        expiredDate = expirationDate.format(formatter),
        minimumAmount = if (minimumOrderedAmount == 0) "" else minimumOrderedAmount.toString(),
        discountAmount = discountAmount,
    )
}
