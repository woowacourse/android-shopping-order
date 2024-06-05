package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface CreateCartProductUseCase {
    operator fun invoke(
        productId: Long,
        count: Int,
    ): Result<Cart>
}

class DefaultCreateCartProductUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : CreateCartProductUseCase {
    override operator fun invoke(
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
        @Volatile
        private var instance: CreateCartProductUseCase? = null

        fun instance(
            cartRepository: CartRepository,
            productRepository: ProductRepository,
        ): CreateCartProductUseCase {
            return instance ?: synchronized(this) {
                instance ?: DefaultCreateCartProductUseCase(cartRepository, productRepository)
                    .also { instance = it }
            }
        }
    }
}
