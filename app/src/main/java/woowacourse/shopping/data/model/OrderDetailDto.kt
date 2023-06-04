package woowacourse.shopping.data.model

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderStatus
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailDto(
    val orderId: Int,
    val orderAt: String,
    val orderStatus: String,
    val payAmount: Int,
    val usedPoint: Int,
    val savedPoint: Int,
    val products: List<OrderProductDto>
)

fun OrderDetailDto.toDomain() = OrderDetail(
    orderId,
    orderAt,
    OrderStatus.PENDING,
    payAmount,
    usedPoint,
    savedPoint,
    products.map { it.toDomain() }
)
