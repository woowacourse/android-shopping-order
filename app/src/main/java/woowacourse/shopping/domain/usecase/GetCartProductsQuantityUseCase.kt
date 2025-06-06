package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository

class GetCartProductsQuantityUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): Result<Int> = repository.fetchCartItemCount()
}
