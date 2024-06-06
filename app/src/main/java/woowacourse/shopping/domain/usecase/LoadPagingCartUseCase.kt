package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository

interface LoadPagingCartUseCase {
    suspend operator fun invoke(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart>
}

class DefaultLoadPagingCartUseCase(
    private val cartRepository: CartRepository,
) : LoadPagingCartUseCase {
    override suspend fun invoke(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart> {
        return cartRepository.loadCurrentPageCart(currentPage, pageSize)
    }

    companion object {
        private var instance: LoadPagingCartUseCase? = null

        fun instance(cartRepository: CartRepository): LoadPagingCartUseCase {
            return instance ?: DefaultLoadPagingCartUseCase(cartRepository)
                .also { instance = it }
        }
    }
}
