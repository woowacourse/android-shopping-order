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
            newQuantity == 0 -> removeFromCartUseCase(cartProduct).map { null }
            else ->
                cartProductRepository.updateQuantity(cartProduct, newQuantity).map {
                    cartProduct.copy(quantity = newQuantity)
                }
        }
}
