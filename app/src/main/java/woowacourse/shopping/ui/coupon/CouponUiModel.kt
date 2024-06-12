package woowacourse.shopping.ui.coupon

import woowacourse.shopping.domain.model.Coupon
import java.time.LocalDate

data class CouponUiModel(
    val couponId: Int,
    val description: String,
    val expirationDate: LocalDate,
    val minimumPrice: Int?,
    val isSelected: Boolean,
) {
    companion object {
        fun from(coupon: Coupon): CouponUiModel {
            return CouponUiModel(
                coupon.id,
                coupon.description,
                coupon.expirationDate,
                coupon.minimumPrice,
                false,
            )
        }
    }
}
