package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface IncreaseCartProductUseCase {
    suspend operator fun invoke(
        productId: Long,
        amount: Int = DEFAULT_AMOUNT,
    ): Result<Cart>

    companion object {
        private const val DEFAULT_AMOUNT = 1
    }
}

class DefaultIncreaseCartProductUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : IncreaseCartProductUseCase {
    override suspend fun invoke(
        productId: Long,
        amount: Int,
    ): Result<Cart> {
        val product =
            productRepository.findProductById(productId)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        val cartProduct =
            cartRepository.findCartProduct(product.id).getOrNull()
                ?: return cartRepository.createCartProduct(product, amount)
        val newCartProduct = cartProduct.increaseCount(amount)
        return cartRepository.updateCartProduct(cartProduct.product, newCartProduct.count)
    }

    companion object {
        private var instance: IncreaseCartProductUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): IncreaseCartProductUseCase {
            return instance ?: DefaultIncreaseCartProductUseCase(
                productRepository,
                cartRepository,
            ).also { instance = it }
        }
    }
}
