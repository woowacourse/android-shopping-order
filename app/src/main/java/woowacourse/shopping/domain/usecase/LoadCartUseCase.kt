package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository

interface LoadCartUseCase {
    operator fun invoke(): Result<Cart>

    operator fun invoke(productIds: List<Long>): Result<Cart>
}

class DefaultLoadCartUseCase(
    private val cartRepository: CartRepository,
) : LoadCartUseCase {

    override operator fun invoke(): Result<Cart> {
        return cartRepository.loadCart()
    }

    override fun invoke(productIds: List<Long>): Result<Cart> {
        return cartRepository.filterCartProducts(productIds)
    }

    companion object {
        @Volatile
        private var Instance: LoadCartUseCase? = null

        fun instance(cartRepository: CartRepository): LoadCartUseCase {
            return Instance ?: synchronized(this) {
                Instance ?: DefaultLoadCartUseCase(cartRepository).also { Instance = it }
            }
        }
    }
}