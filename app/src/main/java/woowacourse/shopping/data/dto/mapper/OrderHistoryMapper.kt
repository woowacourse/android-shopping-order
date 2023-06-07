package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.member.response.GetOrderHistoryResponse
import woowacourse.shopping.domain.OrderHistory

object OrderHistoryMapper : Mapper<OrderHistory, GetOrderHistoryResponse> {
    override fun OrderHistory.toEntity(): GetOrderHistoryResponse {
        return GetOrderHistoryResponse(
            orderId = id,
            orderPrice = price,
            totalAmount = quantity,
            previewName = name
        )
    }

    override fun GetOrderHistoryResponse.toDomain(): OrderHistory {
        return OrderHistory(
            id = orderId,
            price = orderPrice,
            quantity = totalAmount,
            name = previewName
        )
    }
}