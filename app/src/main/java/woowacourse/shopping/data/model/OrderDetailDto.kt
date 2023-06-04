package woowacourse.shopping.data.model

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderInfo
import com.example.domain.model.OrderState
import com.example.domain.model.Point
import com.example.domain.model.Price

data class OrderDetailDto(
    val orderAt: String,
    val orderId: Int,
    val orderStatus: String,
    val payAmount: Int,
    val products: List<OrderDetailInfoDto>,
    val savedPoint: Int,
    val usedPoint: Int
)

data class OrderDetailInfoDto(
    val product: ProductDto,
    val quantity: Int
)

fun OrderDetailDto.toDomain() =
    OrderInfo(
        orderId,
        orderAt,
        OrderState.valueOf(orderStatus),
        Price(payAmount),
        Point(usedPoint),
        Point(savedPoint),
        products.map { it.toDomain() }
    )

private fun OrderDetailInfoDto.toDomain() = OrderDetailProduct(quantity, product.toDomain())
