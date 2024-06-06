package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface OrderCartProductsUseCase {
    suspend operator fun invoke(productIds: List<Long>): Result<Unit>
}

class DefaultOrderCartProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : OrderCartProductsUseCase {
    override suspend fun invoke(productIds: List<Long>): Result<Unit> {
        productIds.forEach {
            productRepository.findProductById(it)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        }
        return cartRepository.orderCartProducts(productIds)
    }

    companion object {
        private var instance: OrderCartProductsUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): OrderCartProductsUseCase {
            return instance ?: DefaultOrderCartProductsUseCase(productRepository, cartRepository)
                .also { instance = it }
        }
    }
}
