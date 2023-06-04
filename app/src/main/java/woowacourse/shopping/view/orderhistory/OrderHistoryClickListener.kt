package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.uimodel.OrderUIModel

interface OrderHistoryClickListener {
    fun orderItemOnClick(order: OrderUIModel)
}
