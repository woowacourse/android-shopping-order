package woowacourse.shopping.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.lang.IllegalArgumentException

class ProductDetailViewModelFactory(
    private val productId: Int,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val lastSeenProductVisible: Boolean,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(
                productId,
                productRepository,
                recentProductRepository,
                cartRepository,
                lastSeenProductVisible,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
