package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository

class AddToCartUseCase(
    private val cartProductRepository: CartProductRepository,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) {
    suspend operator fun invoke(
        product: Product,
        quantityToAdd: Int,
    ): Result<CartProduct?> =
        getCartProductByProductId(product.id).fold(
            onSuccess = { cartProduct ->
                if (cartProduct == null) {
                    cartProductRepository.insert(product.id, quantityToAdd).map { cartId ->
                        CartProduct(cartId, product, quantityToAdd)
                    }
                } else {
                    val newQuantity = cartProduct.quantity + quantityToAdd
                    updateCartQuantityUseCase(cartProduct, newQuantity)
                }
            },
            onFailure = { Result.failure(it) },
        )

    private suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> =
        cartProductRepository.getPagedProducts().map { pagedResult ->
            pagedResult.items.firstOrNull { it.product.id == productId }
        }
}
