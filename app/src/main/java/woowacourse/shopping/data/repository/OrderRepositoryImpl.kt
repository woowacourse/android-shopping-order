package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.order.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val cartItemRepository: CartItemsRepositoryImpl
) : OrderRepository {
    override suspend fun orderItems(cartIds: List<Long>): Result<Unit> {
        val result = orderDataSource.orderCheckedItems(cartIds)

        cartIds.forEach { id ->
            cartItemRepository.deleteCartItemByCartId(id)
        }

        if(result.isSuccess) {
            cartItemRepository.initializeCartItems()
        }
        return result
    }
}
