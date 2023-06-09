package woowacourse.shopping.data.order

import woowacourse.shopping.data.cart.CartItem
import woowacourse.shopping.data.order.source.NetworkOrderDataSource
import java.util.concurrent.CompletableFuture

class DefaultOrderRepository(
    private val networkOrderDataSource: NetworkOrderDataSource
) : OrderRepository {
    override fun getOrders(): CompletableFuture<Result<List<Order>>> {
        return CompletableFuture.supplyAsync {
            networkOrderDataSource.loadOrders().mapCatching {
                it.toExternal()
            }
        }
    }

    override fun getOrder(orderId: Long): CompletableFuture<Result<Order>> {
        return CompletableFuture.supplyAsync {
            networkOrderDataSource.loadOrder(orderId).mapCatching {
                it.toExternal()
            }
        }
    }

    override fun createOrder(cartItems: List<CartItem>): CompletableFuture<Result<Long>> {
        return CompletableFuture.supplyAsync {
            networkOrderDataSource.saveOrder(cartItems)
        }
    }
}
