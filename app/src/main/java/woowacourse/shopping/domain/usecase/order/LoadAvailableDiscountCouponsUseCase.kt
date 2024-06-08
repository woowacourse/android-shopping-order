package woowacourse.shopping.domain.usecase.order

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.coupon.Coupons
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository

interface LoadAvailableDiscountCouponsUseCase {
    suspend operator fun invoke(productIds: List<Long>): Result<Coupons>
}

class DefaultLoadAvailableDiscountCouponsUseCase(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : LoadAvailableDiscountCouponsUseCase {
    override suspend fun invoke(productIds: List<Long>): Result<Coupons> = coroutineScope {
        productIds.forEach {
            productRepository.findProductById(it)
                .onFailure { return@coroutineScope Result.failure(it) }
        }
        val cartJob = async {
            cartRepository.filterCartProducts(productIds)
                .mapCatching { Cart(it.cartProducts) }
        }
        val couponsJob = async {
            orderRepository.loadDiscountCoupons()
        }
        val cart =
            cartJob.await().onFailure { return@coroutineScope Result.failure(it) }.getOrThrow()
        val coupons =
            couponsJob.await().onFailure { return@coroutineScope Result.failure(it) }.getOrThrow()

        Result.success(coupons.availableCoupons(cart))
    }

    companion object {
        private var instance: LoadAvailableDiscountCouponsUseCase? = null

        fun instance(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
            orderRepository: OrderRepository,
        ): LoadAvailableDiscountCouponsUseCase {
            return instance ?: DefaultLoadAvailableDiscountCouponsUseCase(
                productRepository,
                cartRepository,
                orderRepository
            )
                .also { instance = it }
        }
    }
}
