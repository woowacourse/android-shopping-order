package woowacourse.shopping.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository

class HomeViewModelFactory(
    private val productRepository: ProductRepository2,
    private val cartRepository: CartRepository2,
    private val recentProductRepository: RecentProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                productRepository = productRepository,
                cartRepository = cartRepository,
                recentProductRepository = recentProductRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
