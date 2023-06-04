package woowacourse.shopping.data.model

import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.Price
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val orderStatus: String,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)

fun OrderDto.toDomain(): Order = Order(
    orderId,
    Price(payAmount),
    orderAt,
    OrderStatus.PENDING,
    productName,
    productImageUrl,
    totalProductCount
)
