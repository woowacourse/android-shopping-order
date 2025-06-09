package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartProductRepository

class GetTotalCartProductQuantityUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(): Result<Int> = cartProductRepository.getTotalQuantity()
}
