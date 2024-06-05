package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository

interface LoadPagingCartUseCase {
    operator fun invoke(currentPage: Int, pageSize: Int): Result<Cart>
}

class DefaultLoadPagingCartUseCase(
    private val cartRepository: CartRepository,
) : LoadPagingCartUseCase {
    override fun invoke(currentPage: Int, pageSize: Int): Result<Cart> {
        return cartRepository.loadCurrentPageCart(currentPage, pageSize)
    }

    companion object {
        @Volatile
        private var Instance: LoadPagingCartUseCase? = null

        fun instance(cartRepository: CartRepository): LoadPagingCartUseCase {
            return Instance ?: synchronized(this) {
                Instance ?: DefaultLoadPagingCartUseCase(cartRepository)
                    .also { Instance = it }
            }
        }
    }
}