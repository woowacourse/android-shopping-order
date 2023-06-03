package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.dto.OrderCartItem
import woowacourse.shopping.data.order.dto.OrderCartItems
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Order

class OrderRepositoryDefault(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun loadOrder(orderId: Long, callback: (Order) -> Unit) {
        orderDataSource.loadOrder(orderId) { order ->
            if (order != null) {
                callback(order.toDomainOrder())
            }
        }
    }

    override fun loadOrders(callback: (List<Order>) -> Unit) {
        orderDataSource.loadOrders { orders ->
            if (orders != null) {
                callback(orders.orders.map { it.toDomainOrder() })
            }
        }
    }

    override fun orderCartProducts(cartProducts: List<CartProduct>, callback: () -> Unit) {
        orderDataSource.orderCartProducts(cartProducts.toOrderCartItems(), callback)
    }

    private fun List<CartProduct>.toOrderCartItems() = OrderCartItems(map { it.toOrderCartItem() })

    private fun CartProduct.toOrderCartItem() = OrderCartItem(
        cartItemId = cartId,
        orderCartItemImageUrl = product.imageUrl,
        orderCartItemName = product.name,
        orderCartItemPrice = product.price.value,
    )
}
