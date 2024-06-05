package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository

interface LoadPagingCartUseCase {
    operator fun invoke(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart>
}

class DefaultLoadPagingCartUseCase(
    private val cartRepository: CartRepository,
) : LoadPagingCartUseCase {
    override fun invoke(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart> {
        return cartRepository.loadCurrentPageCart(currentPage, pageSize)
    }

    companion object {
        @Volatile
        private var instance: LoadPagingCartUseCase? = null

        fun instance(cartRepository: CartRepository): LoadPagingCartUseCase {
            return instance ?: synchronized(this) {
                instance ?: DefaultLoadPagingCartUseCase(cartRepository)
                    .also { instance = it }
            }
        }
    }
}
