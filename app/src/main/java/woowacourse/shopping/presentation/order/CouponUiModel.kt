package woowacourse.shopping.presentation.order

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import java.time.LocalDate

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: LocalDate,
    val minimumAmount: Int?,
    val isSelected: Boolean = false,
) {
    fun hasMinimumAmount(): Boolean = minimumAmount != null
}

fun Coupon.toUiModel(): CouponUiModel =
    CouponUiModel(
        id = this.id,
        description = this.description,
        expirationDate = this.expirationDate,
        minimumAmount = this.getMinimumAmount(),
    )

private fun Coupon.getMinimumAmount(): Int? {
    return when (this) {
        is FixedCoupon -> minimumAmount
        is FreeShippingCoupon -> minimumAmount
        else -> null
    }
}
