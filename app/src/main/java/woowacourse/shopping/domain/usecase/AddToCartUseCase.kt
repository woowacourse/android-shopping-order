package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class AddToCartUseCase(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(
        product: Product,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartRepository.insertProduct(product, quantity) { result ->
            result
                .onSuccess { onSuccess() }
                .onFailure { onFailure(it) }
        }
    }
}
