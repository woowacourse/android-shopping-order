package woowacourse.shopping.data.model

import com.example.domain.model.Price
import com.example.domain.model.cart.CartProduct
import com.example.domain.model.order.Order
import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.order.OrderHistoryInfo
import com.example.domain.model.order.OrderHistoryProduct
import com.example.domain.model.order.OrderState
import com.example.domain.model.point.Point
import com.example.domain.model.point.PointInfo
import com.example.domain.model.product.Product
import woowacourse.shopping.data.model.cart.CartProductDto
import woowacourse.shopping.data.model.order.ContentDto
import woowacourse.shopping.data.model.order.OrderDetailDto
import woowacourse.shopping.data.model.order.OrderDetailInfoDto
import woowacourse.shopping.data.model.order.OrderHistoryInfoDto
import woowacourse.shopping.data.model.order.PointDto
import woowacourse.shopping.data.model.product.ProductDto

fun ProductDto.toDomain() = Product(id, name, imageUrl, Price(price))

fun CartProductDto.toDomain() = CartProduct(id, product.toDomain(), quantity, true)

fun PointDto.toDomain() = PointInfo(currentPoint, toBeExpiredPoint)

fun OrderDetailDto.toDomain() =
    Order(
        orderId,
        orderAt,
        toDomain(orderStatus),
        Price(payAmount),
        Point(usedPoint),
        Point(savedPoint),
        products.map { it.toDomain() }
    )

fun OrderDetailInfoDto.toDomain() = OrderDetailProduct(quantity, product.toDomain())

fun ContentDto.toDomain() = OrderHistoryProduct(
    orderId,
    Price(payAmount),
    orderAt,
    toDomain(orderStatus),
    productName,
    productImageUrl,
    totalProductCount
)

fun OrderHistoryInfoDto.toDomain() =
    OrderHistoryInfo(totalPages, currentPage, pageSize, contents.map { it.toDomain() })

private fun toDomain(orderStatus: String): OrderState {
    return OrderState.values().first { it.value == orderStatus }
}
