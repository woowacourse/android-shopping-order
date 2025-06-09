package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class UpdateQuantityUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(
        cartProduct: CartProduct,
        quantityDelta: Int,
    ): Result<CartProduct?> {
        val newQuantity = cartProduct.quantity + quantityDelta
        return cartProductRepository.updateQuantity(cartProduct, quantityDelta).map {
            if (newQuantity > 0) {
                cartProduct.copy(quantity = newQuantity)
            } else {
                null
            }
        }
    }
}
