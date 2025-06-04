package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class DecreaseProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartRepository.decreaseCartProductQuantityFromCart(productId, decreaseCount, onResult)
    }
}
