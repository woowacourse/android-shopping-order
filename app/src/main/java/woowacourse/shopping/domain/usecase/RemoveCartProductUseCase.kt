package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class RemoveCartProductUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) {
        repository.deleteCartProduct(cartId) { result ->
            callback(result)
        }
    }
}
