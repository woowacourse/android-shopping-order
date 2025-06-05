package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class UpdateCartProductUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        cartId: Long?,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    ) {
        if (cartId == null) {
            repository.addCartProduct(productId, quantity) { result ->
                callback(result)
            }
        } else {
            repository.updateCartProduct(cartId, quantity) { result ->
                callback(result)
            }
        }
    }
}
