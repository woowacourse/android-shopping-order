package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class IncreaseCartProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> = cartRepository.insertCartProductQuantityToCart(productId, increaseCount)
}
