package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class IncreaseProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productId: Long): Result<Unit> =
        cartRepository
            .increaseQuantity(productId)
}
