package woowacourse.shopping.view.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository

class CartViewModelFactory(
    private val cartRepository: CartRepository2,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository2
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartRepository = cartRepository,
                orderRepository = orderRepository,
                recentProductRepository = recentProductRepository,
                productRepository = productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
