package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface DeleteCartProductUseCase {
    suspend operator fun invoke(productId: Long): Result<Cart>
}

class DefaultDeleteCartProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : DeleteCartProductUseCase {
    override suspend fun invoke(productId: Long): Result<Cart> {
        productRepository.findProductById(productId)
            .onFailure { return Result.failure(it) }
            .getOrThrow()
        return cartRepository.deleteCartProduct(productId)
    }

    companion object {
        private var instance: DeleteCartProductUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): DeleteCartProductUseCase {
            return instance ?: DefaultDeleteCartProductUseCase(productRepository, cartRepository)
                .also { instance = it }
        }
    }
}
