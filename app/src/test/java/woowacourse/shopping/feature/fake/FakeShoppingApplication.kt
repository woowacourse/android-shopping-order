package woowacourse.shopping.feature.fake

import kotlinx.coroutines.CompletableDeferred
import woowacourse.shopping.AppDependencies
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository

class FakeShoppingApplication : ShoppingApplication() {
    fun setupFakeDependencies(
        productRepository: ProductRepository = FakeProductRepository(
            MockData.MOCK_PRODUCTS
        ),
        cartRepository: CartRepository = FakeCartRepository(),
        recentProductRepository: RecentProductRepository = FakeRecentProductRepository(),
        orderRepository: OrderRepository = FakeOrderRepository(),
    ) {
        appDependenciesDeferred = CompletableDeferred(
            AppDependencies(
                productRepository = productRepository,
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
                orderRepository = orderRepository,
            )
        )
    }
}
