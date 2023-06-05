package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.repository.OrderRepository
import java.util.concurrent.CompletableFuture

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {

    override fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
    ): CompletableFuture<Result<Int>> {
        val orderRequest = OrderRequest(
            basketIds = basketIds.map(Int::toLong),
            usingPoint = usingPoint.toLong(),
            totalPrice = totalPrice.toLong()
        )

        return CompletableFuture.supplyAsync {
            orderRemoteDataSource.addOrder(orderRequest).mapCatching {
                it.toInt()
            }
        }
    }

    override fun getOrder(orderId: Int): CompletableFuture<Result<Order>> {

        return CompletableFuture.supplyAsync {
            orderRemoteDataSource.getOrder(orderId).mapCatching {
                it.toDomainModel()
            }
        }
    }

    override fun getOrders(): CompletableFuture<Result<List<Order>>> {

        return CompletableFuture.supplyAsync {
            orderRemoteDataSource.getOrders().mapCatching { orders ->
                orders.map { it.toDomainModel() }
            }
        }
    }
}
