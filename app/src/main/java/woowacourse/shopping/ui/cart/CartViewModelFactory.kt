package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository, recentProductRepository, cartRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
