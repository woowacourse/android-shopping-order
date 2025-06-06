package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class AddToCartUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int,
    ): Result<Unit> = cartRepository.insertProduct(product, quantity)
}
