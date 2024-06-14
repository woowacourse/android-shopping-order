package woowacourse.shopping.ui.coupon

import woowacourse.shopping.domain.model.coupon.BuyXFreeYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.PercentageCoupon
import woowacourse.shopping.domain.model.coupon.ShippingCoupon
import woowacourse.shopping.ui.coupon.uimodel.CouponUiModel

fun Coupon.toUiModel() =
    when (this) {
        is BuyXFreeYCoupon -> CouponUiModel(id, description, expirationDate, null, null, null)
        is FixedCoupon -> CouponUiModel(id, description, expirationDate, minimumAmount, null, null)
        is ShippingCoupon ->
            CouponUiModel(
                id,
                description,
                expirationDate,
                minimumAmount,
                null,
                null,
            )

        is PercentageCoupon ->
            CouponUiModel(
                id,
                description,
                expirationDate,
                null,
                availableStartTime,
                availableEndTime,
            )
    }
