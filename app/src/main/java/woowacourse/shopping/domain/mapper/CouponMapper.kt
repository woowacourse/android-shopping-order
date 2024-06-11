package woowacourse.shopping.domain.mapper

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.presentation.ui.model.CouponModel
import java.time.format.DateTimeFormatter

fun Coupon.toUiModel(formatter: DateTimeFormatter): CouponModel {
    return CouponModel(
        id = id.toLong(),
        description = description,
        expiredDate = expirationDate.format(formatter),
        minimumAmount = minimumOrderedAmount,
        discountAmount = discountAmount,
        isChecked = false,
    )
}
