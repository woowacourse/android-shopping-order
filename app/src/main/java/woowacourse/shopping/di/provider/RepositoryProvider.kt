package woowacourse.shopping.di.provider

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

object RepositoryProvider {
    lateinit var productRepository: ProductRepository
        private set

    lateinit var cartRepository: CartRepository
        private set

    lateinit var recentProductRepository: RecentProductRepository
        private set

    lateinit var orderRepository: OrderRepository
        private set

    lateinit var couponRepository: CouponRepository
        private set

    fun initProductRepository(repository: ProductRepository) {
        productRepository = repository
    }

    fun initCartRepository(repository: CartRepository) {
        cartRepository = repository
    }

    fun initRecentProductRepository(repository: RecentProductRepository) {
        recentProductRepository = repository
    }

    fun initOrderRepository(repository: OrderRepository) {
        orderRepository = repository
    }

    fun initCouponRepository(repository: CouponRepository) {
        couponRepository = repository
    }
}
