package woowacourse.shopping.ui.coupon

import woowacourse.shopping.domain.model.coupon.Buy2Free1
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.Discount5000
import woowacourse.shopping.domain.model.coupon.FreeShipping
import woowacourse.shopping.domain.model.coupon.MiracleCoupon
import woowacourse.shopping.ui.coupon.uimodel.CouponUiModel

fun Coupon.toUiModel() =
    when (this) {
        is Buy2Free1 -> CouponUiModel(id, description, expirationDate, null, null, null)
        is Discount5000 -> CouponUiModel(id, description, expirationDate, minimumAmount, null, null)
        is FreeShipping -> CouponUiModel(id, description, expirationDate, minimumAmount, null, null)
        is MiracleCoupon ->
            CouponUiModel(
                id,
                description,
                expirationDate,
                null,
                availableStartTime,
                availableEndTime,
            )
    }
