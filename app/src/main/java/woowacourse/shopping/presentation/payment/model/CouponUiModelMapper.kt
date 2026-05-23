package woowacourse.shopping.presentation.payment.model

import woowacourse.shopping.domain.model.Coupon
import java.time.format.DateTimeFormatter

fun Coupon.toUiModel(): CouponUiModel =
    CouponUiModel(
        id = id,
        name = description,
        expiredDate = expirationDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
        minPayAmount =
            when (this) {
                is Coupon.Fixed -> minimumAmount.amount.toString()
                is Coupon.FreeShipping -> minimumAmount.amount.toString()
                else -> null
            },
    )
