package woowacourse.shopping.domain.usecase

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
        // 1. product id 유효성 검사
        productRepository.findProductById(productId)
            .onFailure { return Result.failure(it) }
            .getOrThrow()
        // 2. cart 에서 product id 를 가진 상품을 삭제한다.
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
