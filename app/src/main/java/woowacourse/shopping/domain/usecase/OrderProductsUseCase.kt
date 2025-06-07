package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.OrderRepository

class OrderProductsUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(cartIds: Set<Long>): Result<Unit> = orderRepository.postOrderProducts(cartIds.toList())
}
