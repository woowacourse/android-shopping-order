package woowacourse.shopping.util

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.FakeCartRepository
import woowacourse.shopping.data.repository.FakeCouponRepository
import woowacourse.shopping.data.repository.FakeOrderRepository
import woowacourse.shopping.data.repository.FakeProductRepository
import woowacourse.shopping.data.repository.FakeRecentProductRepository
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.CartViewModelFactory
import woowacourse.shopping.view.detail.DetailViewModelFactory
import woowacourse.shopping.view.home.HomeViewModelFactory
import woowacourse.shopping.view.order.OrderViewModelFactory

class FakeShoppingApplication : ShoppingApplication() {
    override val productRepository: ProductRepository = FakeProductRepository(count = 100, cartCount = 20)
    override val cartRepository: CartRepository = FakeCartRepository(count = 20)
    override val couponRepository: CouponRepository = FakeCouponRepository()
    override val orderRepository: OrderRepository = FakeOrderRepository()
    override val recentProductRepository: RecentProductRepository = FakeRecentProductRepository()
    override val homeViewModelFactory: HomeViewModelFactory =
        HomeViewModelFactory(
            productRepository,
            cartRepository,
            recentProductRepository,
        )
    override val cartViewModelFactory: CartViewModelFactory =
        CartViewModelFactory(
            cartRepository,
            recentProductRepository,
            productRepository,
        )

    override fun detailViewModelFactory(productId: Int): DetailViewModelFactory {
        return DetailViewModelFactory(
            cartRepository,
            productRepository,
            recentProductRepository,
            productId,
        )
    }

    override fun orderViewModelFactory(cartItems: List<CartItemDomain>): OrderViewModelFactory {
        return OrderViewModelFactory(
            couponRepository,
            orderRepository,
            cartItems,
        )
    }
}
