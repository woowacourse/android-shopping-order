package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.OrderPostRequest
import woowacourse.shopping.data.dto.OrderProductResponse
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.data.dto.toOrderPostInfo
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.ProductCount

fun Order.toOrderRequest(): OrderPostRequest = OrderPostRequest(
    cartItemIds = orderProducts.map { it.toOrderPostInfo() },
    payment = payment.toPaymentRequest(),
)

fun List<OrderResponse>.toOrders(): List<Order> = map { it.toOrder() }

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
