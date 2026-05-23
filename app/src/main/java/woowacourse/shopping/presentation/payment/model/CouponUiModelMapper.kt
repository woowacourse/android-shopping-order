package woowacourse.shopping.presentation.payment.model

import woowacourse.shopping.domain.model.Coupon
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Coupon.toUiModel(): CouponUiModel =
    CouponUiModel(
        id = id,
        name = description,
        expiredDate = expirationDate.format(EXPIRATION_DATE_FORMATTER),
        minPayAmount =
            when (this) {
                is Coupon.Fixed -> minimumAmount.amount.toString()
                is Coupon.FreeShipping -> minimumAmount.amount.toString()
                else -> null
            },
    )

private val EXPIRATION_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
