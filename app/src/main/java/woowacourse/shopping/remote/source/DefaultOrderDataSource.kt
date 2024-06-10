package woowacourse.shopping.remote.source

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.remote.model.request.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

class DefaultOrderDataSource(
    private val orderApiService: OrderApiService
) : OrderDataSource {
    override suspend fun order(): Result<Unit> = runCatching {
        orderApiService.createOrder2(
            OrderRequest(orders.map { it.cartItemId })
        )
    }

    override suspend fun saveOrderItem(orderItem: OrderItem): Result<Unit> = runCatching {
        val found = orders.find {
            it.cartItemId == orderItem.cartItemId
        }

        if (found == null) {
            _orders.add(orderItem)
        } else {
            _orders.remove(found)
            _orders.add(orderItem)
        }
    }

    override suspend fun removeOrderItem(orderItem: OrderItem): Result<Unit> = runCatching {
        _orders.remove(orderItem)
    }

    override suspend fun removeOrderItem(cartItemId: Long): Result<Unit> = runCatching {
        _orders.remove(
            _orders.find { it.cartItemId == cartItemId }
        )
    }


    override suspend fun loadAllOrders(): Result<List<OrderItem>> = runCatching {
        orders
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        _orders.clear()
    }

    companion object {
        private const val TAG = "OrderRemoteDataSource"

        private val _orders: MutableList<OrderItem> = mutableListOf()
        val orders: List<OrderItem> get() = _orders
    }
}
