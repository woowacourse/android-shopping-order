package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class GetCartProductByProductIdUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(productId: Int): Result<CartProduct?> =
        cartProductRepository.getPagedProducts().mapCatching { pagedResult ->
            pagedResult.items.firstOrNull { it.product.id == productId }
        }
}
