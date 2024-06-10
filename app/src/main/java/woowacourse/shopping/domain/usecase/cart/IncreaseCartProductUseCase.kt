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
        // 1. product id 가 유효한지 확인한다.
        val product =
            productRepository.findProductById(productId)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        // 2. cart 에서 product id 를 가진 상품을 찾는다.
        val cartProduct =
            cartRepository.findCartProduct(product.id).getOrNull()
                // 3. cartProduct 가 없으면 새로 생성한다.
                ?: return cartRepository.createCartProduct(product, amount)
        // 4. cartProduct 수량을 증가시킨다.
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