package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class UpdateCartProductUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        productId: Long,
        cartId: Long?,
        quantity: Int,
    ): Result<Unit> =
        if (cartId == null) {
            repository.addCartProduct(productId, quantity)
        } else {
            repository.updateCartProduct(cartId, quantity)
        }
}
