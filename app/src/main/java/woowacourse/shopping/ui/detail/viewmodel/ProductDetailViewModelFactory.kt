package woowacourse.shopping.ui.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ProductDetailViewModelFactory(
    private val productId: Long,
    private val lastSeenProductState: Boolean,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductDetailViewModel(
            productId,
            lastSeenProductState,
            productRepository,
            recentProductRepository,
            cartRepository,
        ) as T
    }
}
