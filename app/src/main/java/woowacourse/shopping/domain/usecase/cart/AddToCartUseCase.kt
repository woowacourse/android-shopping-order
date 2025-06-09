package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository

class AddToCartUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int,
    ): Result<CartProduct> =
        cartProductRepository.insert(product.id, quantity).map { cartId ->
            CartProduct(cartId, product, quantity)
        }
}
