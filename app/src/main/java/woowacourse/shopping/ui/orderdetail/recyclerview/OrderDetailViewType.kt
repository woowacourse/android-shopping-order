package woowacourse.shopping.ui.orderdetail.recyclerview

import woowacourse.shopping.R

enum class OrderDetailViewType(val value: Int) {
    ORDER_DETAIL(R.layout.item_order_detail),
    PAYMENT(R.layout.item_payment);

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR_MESSAGE = "올바르지 않은 뷰 타입입니다."

        fun of(value: Int): OrderDetailViewType =
            requireNotNull(OrderDetailViewType.values().find { it.value == value }) {
                INVALID_VIEW_TYPE_ERROR_MESSAGE
            }
    }
}
