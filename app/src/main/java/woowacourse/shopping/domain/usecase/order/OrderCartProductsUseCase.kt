package woowacourse.shopping.domain.usecase.order

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface OrderCartProductsUseCase {
    suspend operator fun invoke(productIds: List<Long>): Result<Unit>
}

class DefaultOrderCartProductsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : OrderCartProductsUseCase {
    override suspend fun invoke(productIds: List<Long>): Result<Unit> {
        productIds.forEach {
            productRepository.findProductById(it)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        }
        val cartIds = cartRepository.filterCartProducts(productIds)
            .onFailure { return Result.failure(it) }
            .getOrThrow()
            .cartProducts
            .map { it.id }

        return orderRepository.orderCartProducts(cartIds)
    }

    companion object {
        private var instance: OrderCartProductsUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
            orderRepository: OrderRepository,
        ): OrderCartProductsUseCase {
            return instance ?: DefaultOrderCartProductsUseCase(
                productRepository,
                cartRepository,
                orderRepository
            )
                .also { instance = it }
        }
    }
}
