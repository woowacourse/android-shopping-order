package woowacourse.shopping.presentation.order.payment

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.entity.coupon.Coupon
import woowacourse.shopping.domain.entity.coupon.Coupons
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

fun Coupons.toUiModel(): List<CouponUiModel> {
    return coupons.map { it.toUiModel() }
}

fun Coupon.toUiModel(): CouponUiModel {
    return CouponUiModel(
        id = id,
        name = description,
        discountAmount = discountableMinPrice.toInt(),
        expirationDate = expirationDate.format(dateFormatter),
        minimumPrice = discountableMinPrice.toInt(),
    )
}

fun PaymentErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        PaymentErrorEvent.OrderProducts -> context.getString(R.string.error_msg_order_products)
        PaymentErrorEvent.LoadCoupons -> context.getString(R.string.error_msg_load_coupons)
    }
}
