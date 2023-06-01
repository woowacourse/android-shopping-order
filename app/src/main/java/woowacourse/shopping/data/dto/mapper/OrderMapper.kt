package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.OrderItem
import woowacourse.shopping.data.dto.OrderPostRequest
import woowacourse.shopping.data.dto.OrderProductResponse
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.ProductCount

fun Order.toOrderRequest(): OrderPostRequest = OrderPostRequest(
    orderItems = orderProducts.map { it.toOrderPostInfo() },
    payment = payment.toPaymentRequest(),
)

fun OrderProduct.toOrderPostInfo(): OrderItem = OrderItem(
    cartItemId = cartProductId,
)

fun OrderResponse.toOrder(): Order = Order(
    id = orderId,
    orderProducts = orderedProducts.map { it.toOrderProduct() },
    payment = payment.toPayment(),
)

fun OrderProductResponse.toOrderProduct(): OrderProduct = OrderProduct(
    name = name,
    price = Price(price),
    quantity = ProductCount(quantity),
    imageUrl = imageUrl,
)

fun List<OrderResponse>.toOrders(): List<Order> = map { it.toOrder() }
