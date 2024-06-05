package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class DetailViewModelFactory(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productId: Int,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(
                cartRepository = cartRepository,
                productRepository = productRepository,
                recentProductRepository = recentProductRepository,
                productId = productId,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
