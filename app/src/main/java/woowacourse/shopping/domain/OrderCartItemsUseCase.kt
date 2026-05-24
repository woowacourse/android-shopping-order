package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

class OrderCartItemsUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(cartItemIds: List<Long>): Result<Unit, Error> = orderRepository.orderCartItems(cartItemIds)
}
