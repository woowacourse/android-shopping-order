package woowacourse.shopping.data.model

import com.example.domain.model.CartProduct
import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderHistoryProduct
import com.example.domain.model.OrderInfo
import com.example.domain.model.OrderState
import com.example.domain.model.Point
import com.example.domain.model.PointInfo
import com.example.domain.model.Price
import com.example.domain.model.Product
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
    OrderInfo(
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
