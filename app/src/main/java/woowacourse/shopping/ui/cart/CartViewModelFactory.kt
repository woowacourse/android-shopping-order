package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val productRepository: RemoteProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: RemoteCartRepository,
    private val orderRepository: RemoteOrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository, recentProductRepository, cartRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
