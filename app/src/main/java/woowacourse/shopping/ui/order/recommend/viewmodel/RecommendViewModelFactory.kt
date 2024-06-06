package woowacourse.shopping.ui.order.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel

class RecommendViewModelFactory(
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
    private val activityViewModel: OrderViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendViewModel(
                recentProductRepository = recentProductRepository,
                productRepository = productRepository,
                orderViewModel = activityViewModel
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
