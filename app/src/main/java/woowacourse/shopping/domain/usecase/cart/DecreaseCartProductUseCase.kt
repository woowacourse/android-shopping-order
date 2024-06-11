package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface DecreaseCartProductUseCase {
    suspend operator fun invoke(
        productId: Long,
        amount: Int = DEFAULT_AMOUNT,
    ): Result<Cart>

    companion object {
        private const val DEFAULT_AMOUNT = 1
    }
}

class DefaultDecreaseCartProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : DecreaseCartProductUseCase {
    override suspend fun invoke(
        productId: Long,
        amount: Int,
    ): Result<Cart> {
        val product =
            productRepository.findProductById(productId)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        val cartProduct =
            cartRepository.findCartProduct(product.id).onFailure {
                return Result.failure(it)
            }.getOrThrow()
        if (cartProduct.canDecreaseCount(amount).not()) {
            return cartRepository.deleteCartProduct(productId)
        }
        val newCartProduct = cartProduct.decreaseCount(amount)
        return cartRepository.updateCartProduct(cartProduct.product, newCartProduct.count)
    }

    companion object {
        @Volatile
        private var instance: DecreaseCartProductUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): DecreaseCartProductUseCase {
            return instance ?: DefaultDecreaseCartProductUseCase(productRepository, cartRepository)
                .also { instance = it }
        }
    }
}
