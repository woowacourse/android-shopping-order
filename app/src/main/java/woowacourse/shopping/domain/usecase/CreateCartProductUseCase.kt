package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface CreateCartProductUseCase {
    suspend operator fun invoke(
        productId: Long,
        count: Int,
    ): Result<Cart>
}

class DefaultCreateCartProductUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : CreateCartProductUseCase {
    override suspend fun invoke(
        productId: Long,
        count: Int,
    ): Result<Cart> {
        val product =
            productRepository.findProductById(productId).onFailure {
                return Result.failure(it)
            }.getOrThrow()
        return cartRepository.createCartProduct(product, count)
    }

    companion object {
        private var instance: CreateCartProductUseCase? = null

        fun instance(
            cartRepository: CartRepository,
            productRepository: ProductRepository,
        ): CreateCartProductUseCase {
            return instance ?: DefaultCreateCartProductUseCase(cartRepository, productRepository)
                .also { instance = it }
        }
    }
}
