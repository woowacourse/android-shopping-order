package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class DecreaseProductQuantityUseCase(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartRepository.decreaseQuantity(productId) { result ->
            result
                .onSuccess { onSuccess() }
                .onFailure { onFailure(it) }
        }
    }
}
