package woowacourse.shopping.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule

class ProductViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val cartRepository = RepositoryModule.cartRepository
        val productRepository = RepositoryModule.productRepository
        val recentProductRepository = RepositoryModule.recentProductRepository
        return ProductViewModel(cartRepository, productRepository, recentProductRepository) as T
    }
}
