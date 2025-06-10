package woowacourse.shopping.domain.usecase.payment

import woowacourse.shopping.domain.repository.OrderRepository

class OrderProductsUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(cartIds: List<Int>): Result<Unit> = orderRepository.createOrder(cartIds)
}
