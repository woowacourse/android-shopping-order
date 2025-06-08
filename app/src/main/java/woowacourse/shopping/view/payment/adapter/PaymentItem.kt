package woowacourse.shopping.view.payment.adapter

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Order

sealed class PaymentItem(
    val type: ViewType,
) {
    data object CouponHeaderItem : PaymentItem(ViewType.COUPON_HEADER)

    data class CouponItem(
        val coupon: Coupon,
        val isSelected: Boolean = false,
    ) : PaymentItem(ViewType.COUPON)

    data class PaymentInformationItem(
        val order: Order,
    ) : PaymentItem(ViewType.PAYMENT_INFORMATION)

    enum class ViewType {
        COUPON_HEADER,
        COUPON,
        PAYMENT_INFORMATION,
    }
}
