package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class DecreaseProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> = cartRepository.decreaseCartProductQuantityFromCart(productId, decreaseCount)
}
