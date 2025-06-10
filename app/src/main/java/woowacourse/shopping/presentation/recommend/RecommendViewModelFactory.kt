package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.di.UseCaseModule

class RecommendViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()
        val recentProductRepository = RepositoryModule.recentProductRepository
        val cartRepository = RepositoryModule.cartRepository
        val recommendProductsUseCase = UseCaseModule.recommendProductsUseCase
        return RecommendViewModel(
            savedStateHandle,
            cartRepository,
            recentProductRepository,
            recommendProductsUseCase,
        ) as T
    }
}
