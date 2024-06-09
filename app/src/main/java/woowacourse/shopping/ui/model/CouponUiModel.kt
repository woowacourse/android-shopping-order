package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon.Companion.toUiModel
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon.Companion.toUiModel
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon.Companion.toUiModel
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon.Companion.toUiModel

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: String,
    val minimumAmount: Int?,
    val discountType: String,
    val checked: Boolean = false,
) {
    companion object {
        fun toUiModel(coupon: Coupon): CouponUiModel =
            when(coupon) {
                is FixedDiscountCoupon -> coupon.toUiModel()
                is BuyXGetYCoupon -> coupon.toUiModel()
                is FreeShippingCoupon -> coupon.toUiModel()
                is PercentageDiscountCoupon -> coupon.toUiModel()
            }
    }
}
