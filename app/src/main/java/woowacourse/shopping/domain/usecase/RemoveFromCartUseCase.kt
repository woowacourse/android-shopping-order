package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class RemoveFromCartUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(cartProduct: CartProduct): Result<Unit> = cartProductRepository.delete(cartProduct.id)
}
