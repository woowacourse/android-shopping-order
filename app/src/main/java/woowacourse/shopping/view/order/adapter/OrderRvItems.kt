package woowacourse.shopping.view.order.adapter

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.view.order.state.CouponState

sealed class OrderRvItems(val viewType: ViewType) {
    data object OrderTitleItem : OrderRvItems(ViewType.VIEW_TYPE_ORDER_TITLE)

    data class CouponItem(
        val coupon: CouponState,
    ) : OrderRvItems(ViewType.VIEW_TYPE_COUPONS)

    data class PaymentItem(
        val value: Payment,
    ) : OrderRvItems(ViewType.VIEW_TYPE_PAYMENT)

    enum class ViewType {
        VIEW_TYPE_ORDER_TITLE,
        VIEW_TYPE_COUPONS,
        VIEW_TYPE_PAYMENT,
    }
}
