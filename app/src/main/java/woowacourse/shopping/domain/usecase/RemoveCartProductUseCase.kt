package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class RemoveCartProductUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(cartId: Long): Result<Unit> = repository.deleteCartProduct(cartId)
}
