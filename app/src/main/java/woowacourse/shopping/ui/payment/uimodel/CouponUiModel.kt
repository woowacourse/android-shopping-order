package woowacourse.shopping.ui.payment.uimodel

import woowacourse.shopping.core.formatter.toFormattedDateString
import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.domain.model.coupon.Coupon

data class CouponUiModel(
    val code: String,
    val title: String,
    val expirationDate: String,
    val minimumOrderAmount: String? = null,
    val enabled: Boolean = true,
)

fun Coupon.toUiModel(enabled: Boolean = true): CouponUiModel =
    CouponUiModel(
        code = code,
        title = name,
        expirationDate = expirationDate.toFormattedDateString(),
        minimumOrderAmount = minimumOrderAmount?.toPriceString(),
        enabled = enabled,
    )
