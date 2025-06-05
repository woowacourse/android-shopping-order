package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class IncreaseCartProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartRepository.insertCartProductQuantityToCart(productId, increaseCount, onResult)
    }
}
