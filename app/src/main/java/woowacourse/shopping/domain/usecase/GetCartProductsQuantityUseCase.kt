package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class GetCartProductsQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(callback: (quantity: Result<Int>) -> Unit) {
        repository.fetchCartItemCount { result ->
            callback(result)
        }
    }
}
