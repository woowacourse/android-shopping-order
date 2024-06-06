package woowacourse.shopping.data.order

import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
) : OrderRepository {
    override suspend fun completeOrder(productIds: List<Long>): Result<Unit> {
        return runCatching {
            val cartRepository = CartRepositoryImpl() // Repository에서 다른 Repository를 의존해도 되는지?
            cartRepository.loadAll().onSuccess { carts ->
                val checkedCartIds =
                    carts.filter { productIds.contains(it.product.id) }.map { it.cartId }
                remoteOrderDataSource.requestOrder(checkedCartIds)
            }
        }
    }
}
