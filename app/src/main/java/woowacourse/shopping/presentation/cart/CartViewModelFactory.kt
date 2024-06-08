package woowacourse.shopping.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val productRepository: DefaultProductRepository,
    private val recentProductRepository: com.example.domain.repository.RecentProductRepository,
    private val cartRepository: DefaultCartRepository,
    private val orderRepository: DefaultOrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository, recentProductRepository, cartRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
