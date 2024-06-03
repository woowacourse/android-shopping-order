package woowacourse.shopping.view.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class OrderViewModelFactory(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                cartRepository = cartRepository,
                orderRepository = orderRepository,
                recentProductRepository = recentProductRepository,
                productRepository = productRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
