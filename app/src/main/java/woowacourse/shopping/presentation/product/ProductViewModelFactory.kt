package woowacourse.shopping.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.di.UseCaseModule

class ProductViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val cartRepository = RepositoryModule.cartRepository
        val recentProductRepository = RepositoryModule.recentProductRepository
        val fetchProductsWithCartItemUseCase = UseCaseModule.fetchProductsWithCartItemUseCase
        return ProductViewModel(
            cartRepository,
            recentProductRepository,
            fetchProductsWithCartItemUseCase,
        ) as T
    }
}
