package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase

class RecommendViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val productRepository = RepositoryModule.provideProductRepository()
        val recentProductRepository = RepositoryModule.provideRecentProductRepository()
        val cartRepository = RepositoryModule.provideCartRepository()
        val increaseProductQuantityUseCase = IncreaseProductQuantityUseCase(cartRepository)
        val decreaseProductQuantityUseCase = DecreaseProductQuantityUseCase(cartRepository)
        val addToCartUseCase = AddToCartUseCase(cartRepository)
        return RecommendViewModel(
            productRepository,
            recentProductRepository,
            increaseProductQuantityUseCase,
            decreaseProductQuantityUseCase,
            addToCartUseCase,
        ) as T
    }
}
