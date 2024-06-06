package woowacourse.shopping.domain.usecase

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
        // 1. product id 가 유효한지 확인한다.
        val product =
            productRepository.findProductById(productId)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        // 2. cart 에서 product id 를 가진 상품을 찾는다.
        val cartProduct =
            cartRepository.findCartProduct(product.id).onFailure {
                return Result.failure(it)
            }.getOrThrow()
        // 2. cartProduct 수량을 감소시킬 수 없으면 삭제한다.
        if (cartProduct.canDecreaseCount(amount).not()) {
            return cartRepository.deleteCartProduct(productId)
        }
        // 3. cartProduct 수량을 감소시킨다.
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

