package woowacourse.shopping.presentation.order.payment

import android.content.Context
import woowacourse.shopping.R
import woowacourse.shopping.domain.entity.coupon.BOGOCoupon
import woowacourse.shopping.domain.entity.coupon.Coupon
import woowacourse.shopping.domain.entity.coupon.Coupons
import woowacourse.shopping.domain.entity.coupon.FixedCoupon
import woowacourse.shopping.domain.entity.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.entity.coupon.PercentageCoupon
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

fun Coupons.toUiModel(): List<CouponUiModel> {
    return coupons.map { it.toUiModel() }
}

fun Coupon.toUiModel(): CouponUiModel {
    val minimumPrice =
        when (this) {
            is FixedCoupon -> discountableMinPrice
            is FreeShippingCoupon -> discountableMinPrice
            is BOGOCoupon -> 0
            is PercentageCoupon -> 0
        }
    return CouponUiModel(
        coupon = this,
        expirationDate = expirationDate.format(dateFormatter),
        minimumPrice = minimumPrice,
    )
}

fun PaymentErrorEvent.toErrorMessageFrom(context: Context): String {
    return when (this) {
        PaymentErrorEvent.OrderProducts -> context.getString(R.string.error_msg_order_products)
        PaymentErrorEvent.LoadCoupons -> context.getString(R.string.error_msg_load_coupons)
    }
}
