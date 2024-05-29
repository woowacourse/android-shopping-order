package woowacourse.shopping.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ProductRepository2
import woowacourse.shopping.domain.repository.RecentProductRepository

class DetailViewModelFactory(
    private val cartRepository: CartRepository2,
    private val productRepository: ProductRepository2,
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
