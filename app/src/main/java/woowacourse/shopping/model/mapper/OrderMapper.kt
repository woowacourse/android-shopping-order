package woowacourse.shopping.model.mapper

import com.example.domain.order.OrderSummary
import woowacourse.shopping.model.order.OrderSummaryResponse

fun OrderSummaryResponse.toDomain(): OrderSummary {
    return OrderSummary(id = id, finalPrice = finalPrice, products = products, orderDate = orderDate)
}
