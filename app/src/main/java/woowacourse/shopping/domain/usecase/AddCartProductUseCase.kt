package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface AddCartProductUseCase {
    suspend operator fun invoke(
        productId: Long,
        count: Int,
    ): Result<Cart>
}

class DefaultAddCartProductUseCase(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : AddCartProductUseCase {
    override suspend fun invoke(
        productId: Long,
        count: Int,
    ): Result<Cart> {
        val product =
            productRepository.findProductById(productId).onFailure {
                if (cartRepository.existsCartProduct(productId)) {
                    cartRepository.deleteCartProduct(productId)
                }
                return Result.failure(it)
            }.getOrThrow()
        val existingCart = cartRepository.existsCartProduct(productId)
        if (existingCart) {
            return cartRepository.updateCartProduct(product, count)
        }
        return cartRepository.createCartProduct(product, count)
    }

    companion object {
        private var instance: AddCartProductUseCase? = null

        fun instance(
            cartRepository: CartRepository,
            productRepository: ProductRepository,
        ): AddCartProductUseCase {
            return instance ?: DefaultAddCartProductUseCase(cartRepository, productRepository)
                .also { instance = it }
        }
    }
}
