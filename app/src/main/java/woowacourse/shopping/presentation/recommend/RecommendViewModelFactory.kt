package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase

class RecommendViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val productRepository = RepositoryModule.productRepository
        val recentProductRepository = RepositoryModule.recentProductRepository
        val cartRepository = RepositoryModule.cartRepository
        val recommendProductsUseCase = RecommendProductsUseCase(productRepository)
        return RecommendViewModel(
            recentProductRepository,
            cartRepository,
            recommendProductsUseCase,
        ) as T
    }
}
