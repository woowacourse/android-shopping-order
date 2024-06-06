package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository

interface LoadCartUseCase {
    suspend operator fun invoke(): Result<Cart>

    suspend operator fun invoke(productIds: List<Long>): Result<Cart>
}

class DefaultLoadCartUseCase(
    private val cartRepository: CartRepository,
) : LoadCartUseCase {
    override suspend operator fun invoke(): Result<Cart> {
        return cartRepository.loadCart()
    }

    override suspend fun invoke(productIds: List<Long>): Result<Cart> {
        return cartRepository.filterCartProducts(productIds)
    }

    companion object {
        private var instance: LoadCartUseCase? = null

        fun instance(cartRepository: CartRepository): LoadCartUseCase {
            return instance ?: DefaultLoadCartUseCase(cartRepository).also { instance = it }
        }
    }
}
