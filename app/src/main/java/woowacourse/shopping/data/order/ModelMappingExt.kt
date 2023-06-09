package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.source.NetworkOrder
import woowacourse.shopping.data.order.source.NetworkOrderItem
import woowacourse.shopping.data.product.toExternal

fun NetworkOrderItem.toExternal() = OrderItem(
    quantity,
    product.toExternal()
)

@JvmName("networkOrderItemToExternal")
fun List<NetworkOrderItem>.toExternal() = map(NetworkOrderItem::toExternal)

fun NetworkOrder.toExternal() = Order(
    id,
    totalPrice,
    cartItems.toExternal()
)

@JvmName("networkOrderToExternal")
fun List<NetworkOrder>.toExternal() = map(NetworkOrder::toExternal)
