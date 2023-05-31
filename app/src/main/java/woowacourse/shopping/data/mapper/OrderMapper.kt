package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.OrderPostRequest
import woowacourse.shopping.data.dto.OrderProductResponse
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.data.dto.toOrderProductRequest
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.ProductCount

fun Order.toOrderRequest(): OrderPostRequest = OrderPostRequest(
    orderPostInfos = orderProducts.map { it.toOrderProductRequest() },
    payment = totalPayment.value,
    point = usePoint.value,
)

fun List<OrderResponse>.toOrders(): List<Order> = map { it.toOrder() }

fun OrderResponse.toOrder(): Order = Order(
    id = orderId,
    orderProducts = orderedProducts.map { it.toOrderProduct() },
    totalPayment = Price(totalPrice),
    usePoint = Point(usedPoint),
)

fun OrderProductResponse.toOrderProduct(): OrderProduct = OrderProduct(
    name = name,
    price = Price(price),
    quantity = ProductCount(quantity),
    imageUrl = imageUrl,
)
