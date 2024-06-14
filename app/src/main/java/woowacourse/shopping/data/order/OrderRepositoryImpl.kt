package woowacourse.shopping.data.order

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
    private val cartRepository: CartRepository,
) : OrderRepository {
    override suspend fun completeOrder(productIds: List<Long>): Result<Unit> {
        return runCatching {
            cartRepository.loadAll().onSuccess { carts ->
                val checkedCartIds =
                    carts.filter { productIds.contains(it.product.id) }.map { it.cartId }
                remoteOrderDataSource.order(checkedCartIds)
            }
        }
    }
}
