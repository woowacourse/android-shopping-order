package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.OrderHistoryEntity
import woowacourse.shopping.domain.OrderHistory

object OrderHistoryMapper : Mapper<OrderHistory, OrderHistoryEntity> {
    override fun OrderHistory.toEntity(): OrderHistoryEntity {
        return OrderHistoryEntity(
            orderId = id,
            orderPrice = price,
            totalAmount = quantity,
            previewName = name
        )
    }

    override fun OrderHistoryEntity.toDomain(): OrderHistory {
        return OrderHistory(
            id = orderId,
            price = orderPrice,
            quantity = totalAmount,
            name = previewName
        )
    }
}