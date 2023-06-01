package woowacourse.shopping.ui.order.recyclerview

import woowacourse.shopping.R
import woowacourse.shopping.model.OrderProductModel

enum class OrderViewType(val value: Int) {
    ORDER(OrderProductModel.VIEW_TYPE_VALUE),
    POINT(R.layout.item_point);

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR_MESSAGE = "올바르지 않은 뷰 타입입니다."

        fun of(value: Int): OrderViewType =
            requireNotNull(values().find { it.value == value }) { INVALID_VIEW_TYPE_ERROR_MESSAGE }
    }
}
