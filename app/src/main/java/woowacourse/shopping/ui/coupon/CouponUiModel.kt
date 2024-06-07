package woowacourse.shopping.ui.coupon

import woowacourse.shopping.domain.model.BuyXgetYCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedCoupon
import woowacourse.shopping.domain.model.FreeShippingCoupon
import woowacourse.shopping.domain.model.PercentageCoupon
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
            return when (coupon) {
                is BuyXgetYCoupon ->
                    CouponUiModel(
                        coupon.id,
                        coupon.description,
                        coupon.expirationDate,
                        null,
                        false,
                    )

                is FixedCoupon ->
                    CouponUiModel(
                        coupon.id,
                        coupon.description,
                        coupon.expirationDate,
                        coupon.minimumPrice,
                        false,
                    )

                is FreeShippingCoupon ->
                    CouponUiModel(
                        coupon.id,
                        coupon.description,
                        coupon.expirationDate,
                        coupon.minimumPrice,
                        false,
                    )

                is PercentageCoupon ->
                    CouponUiModel(
                        coupon.id,
                        coupon.description,
                        coupon.expirationDate,
                        null,
                        false,
                    )
            }
        }
    }
}
