package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule

class RecommendViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val productRepository = RepositoryModule.provideProductRepository()
        val recentProductRepository = RepositoryModule.provideRecentProductRepository()
        return RecommendViewModel(productRepository, recentProductRepository) as T
    }
}
