package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import woowacourse.shopping.presentation.converter.convertLocalDateToFormatString
import woowacourse.shopping.presentation.converter.convertLocalTimeToFormatString

fun Coupon.toUiModel(): CouponUiModel {
        return when (this) {
            is FixedDiscountCoupon -> {
                CouponUiModel(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = convertLocalDateToFormatString(expirationDate),
                    discountType = discountType,
                    isChecked = false,
                    minimumAmount = minimumAmount,
                )
            }

            is BuyXGetYCoupon -> {
                CouponUiModel(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = convertLocalDateToFormatString(expirationDate),
                    discountType = discountType,
                    isChecked = false,
                )
            }

            is FreeShippingCoupon -> {
                CouponUiModel(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = convertLocalDateToFormatString(expirationDate),
                    discountType = discountType,
                    isChecked = false,
                )
            }

            is PercentageDiscountCoupon -> {
                CouponUiModel(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = convertLocalDateToFormatString(expirationDate),
                    discountType = discountType,
                    isChecked = false,
                    availableTime = convertLocalTimeToFormatString(availableTime.start)
                            + " ~ " + convertLocalTimeToFormatString(availableTime.end),
                )
            }
        }
    }
