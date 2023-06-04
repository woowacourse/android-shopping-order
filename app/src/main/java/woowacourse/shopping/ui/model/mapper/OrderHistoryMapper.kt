package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.ui.model.OrderHistoryModel

object OrderHistoryMapper : Mapper<OrderHistory, OrderHistoryModel> {
    override fun OrderHistory.toView(): OrderHistoryModel {
        return OrderHistoryModel(id, price, quantity, name)
    }

    override fun OrderHistoryModel.toDomain(): OrderHistory {
        return OrderHistory(id, price, quantity, name)
    }
}