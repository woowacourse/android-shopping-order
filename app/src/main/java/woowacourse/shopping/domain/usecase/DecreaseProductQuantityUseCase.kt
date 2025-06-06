package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class DecreaseProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(productId: Long): Result<Unit> =
        cartRepository
            .decreaseQuantity(productId)
}
