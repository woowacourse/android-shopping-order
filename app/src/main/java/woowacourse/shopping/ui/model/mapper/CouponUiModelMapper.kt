package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.model.coupon.FreeShippingCoupon
import woowacourse.shopping.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.ui.model.UiCoupon
import java.time.LocalTime

fun Coupon.toUiModel(isChecked: Boolean): UiCoupon =
    UiCoupon(
        id = id,
        title = description,
        expiryDateTime = expirationDate.atTime(LocalTime.MAX),
        isChecked = isChecked,
        minimumPrice =
            when (this) {
                is FixedDiscountCoupon -> minimumAmount
                is FreeShippingCoupon -> minimumAmount
                is PercentageDiscountCoupon -> null
                is BuyXGetYCoupon -> null
            },
    )
