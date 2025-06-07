package woowacourse.shopping.presentation.common.model

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expiredDate: LocalDate,
    val minimumAmount: Int?,
    val isSelected: Boolean = false,
) {
    fun formatedExpiredDate(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return expiredDate.format(formatter)
    }

    fun isShowMinimumAmount(): Boolean = minimumAmount != null
}

fun Coupon.toUiModel(): CouponUiModel =
    CouponUiModel(
        id = id,
        description = description,
        expiredDate = expirationDate,
        minimumAmount = minimumAmountIfExists(),
    )

private fun Coupon.minimumAmountIfExists(): Int? = if (this is FixedDiscountCoupon) minimumAmount else null
