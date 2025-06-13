package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class UpdateCartQuantityUseCase(
    private val cartProductRepository: CartProductRepository,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
) {
    suspend operator fun invoke(
        cartProduct: CartProduct,
        newQuantity: Int,
    ): Result<CartProduct?> =
        when {
            newQuantity < MINIMUM_QUANTITY ->
                Result.failure(IllegalArgumentException(ERROR_INVALID_QUANTITY))

            newQuantity == MINIMUM_QUANTITY -> removeFromCartUseCase(cartProduct).map { null }
            else ->
                cartProductRepository.updateQuantity(cartProduct, newQuantity).map {
                    cartProduct.copy(quantity = newQuantity)
                }
        }

    companion object {
        private const val MINIMUM_QUANTITY = 0
        private const val ERROR_INVALID_QUANTITY = "수량은 0 이상이어야 합니다."
    }
}
