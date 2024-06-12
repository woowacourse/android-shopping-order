package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.ui.model.CouponUiModel

object CouponMapper {
    fun toUiModel(coupon: Coupon): CouponUiModel =
        when(coupon) {
            is FixedDiscountCoupon -> coupon.toUiModel()
            is BuyXGetYCoupon -> coupon.toUiModel()
            is FreeShippingCoupon -> coupon.toUiModel()
            is PercentageDiscountCoupon -> coupon.toUiModel()
        }

    private fun BuyXGetYCoupon.toUiModel() =
        CouponUiModel(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumAmount = null,
            discountType = discountType,
        )

    private fun FixedDiscountCoupon.toUiModel() =
        CouponUiModel(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumAmount = minimumAmount,
            discountType = discountType,
        )

    private fun FreeShippingCoupon.toUiModel() =
        CouponUiModel(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumAmount = minimumAmount,
            discountType = discountType,
        )

    private fun PercentageDiscountCoupon.toUiModel() =
        CouponUiModel(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumAmount = null,
            discountType = discountType,
        )
}
