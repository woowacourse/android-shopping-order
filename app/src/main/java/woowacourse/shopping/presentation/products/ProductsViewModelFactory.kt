package woowacourse.shopping.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.lang.IllegalArgumentException

class ProductsViewModelFactory(
    private val productRepository: DefaultProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: DefaultCartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            return ProductsViewModel(productRepository, recentProductRepository, cartRepository) as T
        }
        throw IllegalArgumentException()
    }
}
