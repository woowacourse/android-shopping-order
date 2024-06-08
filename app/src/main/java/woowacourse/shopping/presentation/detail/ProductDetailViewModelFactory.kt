package woowacourse.shopping.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import java.lang.IllegalArgumentException

class ProductDetailViewModelFactory(
    private val productId: Int,
    private val productRepository: DefaultProductRepository,
    private val recentProductRepository: com.example.domain.repository.RecentProductRepository,
    private val cartRepository: DefaultCartRepository,
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