package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.coupon.BogoCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.TimeLimitedCoupon
import java.time.LocalDate

data class CouponUiModel(
    val id: Long,
    val name: String,
    val isSelected: Boolean,
    val expirationDate: LocalDate,
    val minPrice: Int,
) {
    val hasMinPrice get() = minPrice > 0
}

fun Coupon.toUiModel(isSelected: Boolean = false): CouponUiModel {
    val minPrice =
        when (this) {
            is BogoCoupon -> null
            is DiscountCoupon -> minimumAmount
            is FreeShippingCoupon -> minimumAmount
            is TimeLimitedCoupon -> null
        }

    return CouponUiModel(
        id = id,
        name = description,
        isSelected = isSelected,
        expirationDate,
        minPrice = minPrice ?: 0,
    )
}
