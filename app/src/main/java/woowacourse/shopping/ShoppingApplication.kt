package woowacourse.shopping

import android.app.Application
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

abstract class ShoppingApplication : Application() {
    abstract val productRepository: ProductRepository
    abstract val cartRepository: CartRepository
    abstract val couponRepository: CouponRepository
    abstract val orderRepository: OrderRepository
    abstract val recentProductRepository: RecentProductRepository

    abstract val homeViewModelFactory: HomeViewModelFactory
    abstract val cartViewModelFactory: CartViewModelFactory

    abstract fun detailViewModelFactory(productId: Int): DetailViewModelFactory

    abstract fun orderViewModelFactory(cartItems: List<CartItemDomain>): OrderViewModelFactory
}
