package woowacourse.shopping.presentation.ui.model

import woowacourse.shopping.domain.model.Coupon
import java.time.format.DateTimeFormatter

data class CouponModel(
    val id: Long,
    val description: String,
    val expiredDate: String,
    val minimumAmount: Int,
    val discountAmount: Int,
    val isChecked: Boolean,
)

fun Coupon.toUiModel(): CouponModel {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    return CouponModel(
        id = id.toLong(),
        description = description,
        expiredDate = expirationDate.format(formatter),
        minimumAmount = minimumOrderedAmount,
        discountAmount = discountAmount,
        isChecked = false,
    )
}
